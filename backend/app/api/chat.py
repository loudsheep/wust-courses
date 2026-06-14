from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import StreamingResponse
import logging
import traceback
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db import get_db
from app.services.chat import stream_chat
from app.services.provider import get_provider

router = APIRouter(prefix="/chat", tags=["chat"])

logger = logging.getLogger(__name__)


class ChatRequest(BaseModel):
    message: str
    provider_id: str | None = None
    conversation_id: str | None = None


class ChatResponse(BaseModel):
    content: str
    components: list | None = None


@router.post("")
async def chat(req: ChatRequest, db: Session = Depends(get_db)):
    try:
        cfg = get_provider(db, req.provider_id)
        return StreamingResponse(
            stream_chat(req, db, cfg),
            media_type="text/event-stream",
        )

    except HTTPException:
        raise

    except Exception as e:
        logger.error("CHAT ERROR")
        traceback.print_exc()

        raise HTTPException(
            status_code=500,
            detail=str(e),
        )
