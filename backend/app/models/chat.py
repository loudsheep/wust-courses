from sqlalchemy import String, ForeignKey, Text, JSON
from sqlalchemy.orm import Mapped, mapped_column, relationship
from typing import List, Optional
from app.db.base import Base, TimestampMixin
import uuid

class Conversation(Base, TimestampMixin):
    __tablename__ = "conversations"

    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    title: Mapped[Optional[str]] = mapped_column(String, nullable=True)
    
    messages: Mapped[List["Message"]] = relationship("Message", back_populates="conversation", cascade="all, delete-orphan")

class Message(Base, TimestampMixin):
    __tablename__ = "messages"

    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    conversation_id: Mapped[str] = mapped_column(String, ForeignKey("conversations.id"), nullable=False)
    role: Mapped[str] = mapped_column(String, nullable=False)  # 'user' or 'assistant'
    content: Mapped[str] = mapped_column(Text, nullable=False)
    components: Mapped[Optional[dict]] = mapped_column(JSON, nullable=True)

    conversation: Mapped["Conversation"] = relationship("Conversation", back_populates="messages")
