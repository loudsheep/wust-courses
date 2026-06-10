from sqlalchemy.orm import Session
from fastapi import HTTPException

from app.models.llm_provider import LLMProviderConfig


def get_provider(db: Session, provider_id: str | None):
    if provider_id:
        cfg = (
            db.query(LLMProviderConfig)
            .filter(LLMProviderConfig.id == provider_id)
            .first()
        )

        if not cfg:
            raise HTTPException(status_code=404, detail="Provider not found")

        return cfg

    cfg = db.query(LLMProviderConfig).filter(LLMProviderConfig.is_active).first()

    if not cfg:
        raise HTTPException(
            status_code=400,
            detail="No active provider configured",
        )

    return cfg
