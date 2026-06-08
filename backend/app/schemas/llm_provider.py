from datetime import datetime

from pydantic import BaseModel, ConfigDict

from app.models.llm_provider import LLMProvider


class LLMProviderCreate(BaseModel):
    name: str
    provider: LLMProvider
    model: str
    api_key: str | None = None
    base_url: str | None = None


class LLMProviderResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    name: str
    provider: LLMProvider
    model: str
    base_url: str | None
    is_active: bool
    # api_key intentionally omitted
    created_at: datetime
    updated_at: datetime
