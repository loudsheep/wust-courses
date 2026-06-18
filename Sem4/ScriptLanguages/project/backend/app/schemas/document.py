from datetime import datetime

from pydantic import BaseModel, ConfigDict

from app.models.document import DocumentStatus


class DocumentCreate(BaseModel):
    original_filename: str
    file_size: int = 0
    mime_type: str = "application/octet-stream"


class DocumentResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    original_filename: str
    stored_filename: str
    file_path: str
    file_size: int
    mime_type: str
    status: DocumentStatus
    error_message: str | None
    chunk_count: int | None
    chunk_size: int | None
    chunk_overlap: int | None
    is_stale: bool
    deleted_at: datetime | None
    created_at: datetime
    updated_at: datetime


class ReindexRequest(BaseModel):
    document_ids: list[str] | None = None


class ReindexResponse(BaseModel):
    enqueued: list[str]
    skipped: list[str] = []
