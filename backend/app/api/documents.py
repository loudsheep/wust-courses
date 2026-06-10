import uuid
from pathlib import Path
import os
import shutil

from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from fastapi.concurrency import run_in_threadpool
from pydantic import FilePath
from sqlalchemy.orm import Session

from app.db import get_db
from app.models.document import Document
from app.schemas.document import DocumentCreate, DocumentResponse

router = APIRouter(prefix="/documents", tags=["documents"])

UPLOAD_DIR = Path("data/uploads")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)


@router.get("", response_model=list[DocumentResponse])
def list_documents(db: Session = Depends(get_db)):
    return db.query(Document).order_by(Document.created_at.desc()).all()


@router.get("/{document_id}", response_model=DocumentResponse)
def get_document(document_id: str, db: Session = Depends(get_db)):
    doc = db.query(Document).filter(Document.id == document_id).first()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")
    return doc


def _save_file(file_obj, path):
    with open(path, "wb") as buffer:
        shutil.copyfileobj(file_obj, buffer)
    return path.stat().st_size


@router.post("", response_model=DocumentResponse, status_code=201)
async def create_document(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
):
    stored_name = f"{uuid.uuid4()}_{file.filename}"
    file_path = UPLOAD_DIR / stored_name

    try:
        # Przenosimy blokującą operację zapisu do osobnego wątku
        file_size = await run_in_threadpool(_save_file, file.file, file_path)
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to save file: {str(e)}",
        )

    doc = Document(
        original_filename=file.filename,
        stored_filename=stored_name,
        file_path=str(file_path),
        file_size=file_size,
        mime_type=file.content_type,
    )

    db.add(doc)
    db.commit()
    db.refresh(doc)

    return doc


@router.delete("/{document_id}", status_code=204)
async def delete_document(document_id: str, db: Session = Depends(get_db)):
    doc = db.query(Document).filter(Document.id == document_id).first()

    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    try:
        if doc.file_path:
            # Sprawdzanie istnienia i usuwanie pliku w threadpool
            exists = await run_in_threadpool(os.path.exists, doc.file_path)
            if exists:
                await run_in_threadpool(os.remove, doc.file_path)
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to delete file from disk: {str(e)}",
        )

    db.delete(doc)
    db.commit()

    return None
