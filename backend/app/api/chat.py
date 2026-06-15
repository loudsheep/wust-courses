from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import StreamingResponse
import logging
import traceback
from pydantic import BaseModel
from sqlalchemy.orm import Session
from sqlalchemy import func

from app.db import get_db
from app.services.chat import stream_chat
from app.services.provider import get_provider
from app.models.chat import Conversation, Message
from app.schemas.chat import ConversationRead, ConversationSummary, MessageRead

router = APIRouter(prefix="/chat", tags=["chat"])

logger = logging.getLogger(__name__)


class ChatRequest(BaseModel):
    message: str
    provider_id: str | None = None
    conversation_id: str | None = None


class ChatResponse(BaseModel):
    content: str
    components: list | None = None


@router.get("/conversations", response_model=list[ConversationSummary])
def list_conversations(db: Session = Depends(get_db)):
    # Join with messages to get count
    results = (
        db.query(
            Conversation.id,
            Conversation.title,
            Conversation.created_at,
            func.count(Message.id).label("message_count"),
        )
        .outerjoin(Message)
        .group_by(Conversation.id, Conversation.title, Conversation.created_at)
        .order_by(Conversation.created_at.desc())
        .all()
    )
    
    # Map to schema explicitly
    summaries = []
    for r in results:
        summaries.append(
            ConversationSummary(
                id=r.id,
                title=r.title,
                created_at=r.created_at,
                message_count=r.message_count,
            )
        )
    return summaries


@router.get("/conversations/{conversation_id}", response_model=ConversationRead)
def get_conversation(conversation_id: str, db: Session = Depends(get_db)):
    conv = db.query(Conversation).filter(Conversation.id == conversation_id).first()
    if not conv:
        raise HTTPException(status_code=404, detail="Conversation not found")
    return conv


@router.get("/conversations/{conversation_id}/messages", response_model=list[MessageRead])
def list_messages(conversation_id: str, db: Session = Depends(get_db)):
    messages = (
        db.query(Message)
        .filter(Message.conversation_id == conversation_id)
        .order_by(Message.created_at.asc())
        .all()
    )
    return messages


@router.delete("/conversations/{conversation_id}", status_code=204)
def delete_conversation(conversation_id: str, db: Session = Depends(get_db)):
    conv = db.query(Conversation).filter(Conversation.id == conversation_id).first()
    if not conv:
        raise HTTPException(status_code=404, detail="Conversation not found")
    
    db.delete(conv)
    db.commit()


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
