from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class MessageBase(BaseModel):
    role: str
    content: str
    components: Optional[list] = None

class MessageCreate(MessageBase):
    conversation_id: str

class MessageRead(MessageBase):
    id: str
    created_at: datetime

    class Config:
        from_attributes = True

class ConversationBase(BaseModel):
    title: Optional[str] = None

class ConversationCreate(ConversationBase):
    pass

class ConversationRead(ConversationBase):
    id: str
    created_at: datetime
    messages: List[MessageRead] = []

    class Config:
        from_attributes = True
