import uuid
from pathlib import Path
import os
import shutil

from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
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


@router.post("", response_model=DocumentResponse, status_code=201)
def create_document(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
):
    stored_name = f"{uuid.uuid4()}_{file.filename}"
    file_path = UPLOAD_DIR / stored_name

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    doc = Document(
        original_filename=file.filename,
        stored_filename=stored_name,
        file_path=str(file_path),
        file_size=file_path.stat().st_size,
        mime_type=file.content_type,
    )

    db.add(doc)
    db.commit()
    db.refresh(doc)

    return doc


@router.delete("/{document_id}", status_code=204)
def delete_document(document_id: str, db: Session = Depends(get_db)):
    doc = db.query(Document).filter(Document.id == document_id).first()

    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    try:
        if doc.file_path and os.path.exists(doc.file_path):
            os.remove(doc.file_path)
    except Exception as e:
        # możesz też logować zamiast failować request
        raise HTTPException(
            status_code=500,
            detail=f"Failed to delete file from disk: {str(e)}",
        )

    db.delete(doc)
    db.commit()

    return None
