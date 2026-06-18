from sqlalchemy.orm import Session
from fastapi import HTTPException

from app.models.llm_provider import LLMProviderConfig


def get_provider(db: Session, provider_id: str | None):
    if provider_id:
        cfg = (
            db.query(LLMProviderConfig)
            .filter(LLMProviderConfig.id == provider_id)
            .filter(LLMProviderConfig.is_active == True)
            .first()
        )

        if not cfg:
            raise HTTPException(status_code=404, detail="Active provider not found")

        return cfg

    cfg = (
        db.query(LLMProviderConfig)
        .filter(LLMProviderConfig.is_active == True)
        .order_by(LLMProviderConfig.created_at.desc())
        .first()
    )

    if not cfg:
        raise HTTPException(
            status_code=400,
            detail="No active provider configured",
        )

    return cfg
