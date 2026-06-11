import os
import uuid
from datetime import datetime, timezone
from pathlib import Path
import shutil

from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from fastapi.concurrency import run_in_threadpool
from sqlalchemy.orm import Session

from app.db import get_db
from app.models.document import Document, DocumentStatus
from app.schemas.document import (
    DocumentCreate,
    DocumentResponse,
    ReindexRequest,
    ReindexResponse,
)
from app.services.document_extraction import detect_mime_type
from app.tasks.documents import (
    delete_document_task,
    index_document_task,
    reindex_document_task,
)

router = APIRouter(prefix="/documents", tags=["documents"])

BACKEND_DIR = Path(__file__).resolve().parent.parent.parent

UPLOAD_DIR = Path(os.getenv("UPLOAD_DIR", BACKEND_DIR / "data" / "uploads"))
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

ALLOWED_EXTENSIONS = {".pdf", ".docx", ".txt", ".md"}


@router.get("", response_model=list[DocumentResponse])
def list_documents(db: Session = Depends(get_db)):
    return (
        db.query(Document)
        .filter(Document.deleted_at.is_(None))
        .order_by(Document.created_at.desc())
        .all()
    )


@router.get("/{document_id}", response_model=DocumentResponse)
def get_document(document_id: str, db: Session = Depends(get_db)):
    doc = (
        db.query(Document)
        .filter(Document.id == document_id, Document.deleted_at.is_(None))
        .first()
    )
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")
    return doc


def _save_file(file_obj, path):
    with open(path, "wb") as buffer:
        shutil.copyfileobj(file_obj, buffer)
    return path.stat().st_size


@router.post("", response_model=DocumentResponse, status_code=202)
async def create_document(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
):
    extension = Path(file.filename or "").suffix.lower()
    if extension not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=415,
            detail=f"Unsupported file type: {extension or 'unknown'}",
        )

    stored_name = f"{uuid.uuid4()}_{file.filename}"
    file_path = UPLOAD_DIR / stored_name

    try:
        file_size = await run_in_threadpool(_save_file, file.file, file_path)

        mime_type = detect_mime_type(file_path)
        if mime_type is None:
            raise HTTPException(
                status_code=415,
                detail="File content does not match its extension",
            )

        doc = Document(
            original_filename=file.filename,
            stored_filename=stored_name,
            file_path=str(file_path),
            file_size=file_size,
            mime_type=mime_type,
        )
        db.add(doc)
        db.commit()
    except Exception:
        file_path.unlink(missing_ok=True)
        raise

    db.refresh(doc)

    index_document_task.delay(doc.id)

    return doc


@router.post("/reindex", response_model=ReindexResponse, status_code=202)
def reindex_documents(payload: ReindexRequest, db: Session = Depends(get_db)):
    if payload.document_ids is None:
        docs = (
            db.query(Document)
            .filter(Document.is_stale.is_(True), Document.deleted_at.is_(None))
            .all()
        )
        enqueued_ids = [doc.id for doc in docs]
        skipped_ids: list[str] = []
    else:
        docs = (
            db.query(Document)
            .filter(
                Document.id.in_(payload.document_ids),
                Document.deleted_at.is_(None),
            )
            .all()
        )
        found_ids = {doc.id for doc in docs}
        enqueued_ids = [doc_id for doc_id in payload.document_ids if doc_id in found_ids]
        skipped_ids = [doc_id for doc_id in payload.document_ids if doc_id not in found_ids]

    for doc in docs:
        doc.status = DocumentStatus.INDEXING
    db.commit()

    for doc_id in enqueued_ids:
        reindex_document_task.delay(doc_id)

    return ReindexResponse(enqueued=enqueued_ids, skipped=skipped_ids)


@router.post("/{document_id}/reindex", response_model=DocumentResponse, status_code=202)
def reindex_document(document_id: str, db: Session = Depends(get_db)):
    doc = (
        db.query(Document)
        .filter(Document.id == document_id, Document.deleted_at.is_(None))
        .first()
    )
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    doc.status = DocumentStatus.INDEXING
    db.commit()
    db.refresh(doc)

    reindex_document_task.delay(document_id)

    return doc


@router.delete("/{document_id}", status_code=204)
async def delete_document(document_id: str, db: Session = Depends(get_db)):
    doc = (
        db.query(Document)
        .filter(Document.id == document_id, Document.deleted_at.is_(None))
        .first()
    )

    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    doc.deleted_at = datetime.now(timezone.utc)
    db.commit()

    delete_document_task.delay(document_id)

    return None
