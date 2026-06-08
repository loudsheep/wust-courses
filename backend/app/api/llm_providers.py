import os

from cryptography.fernet import Fernet
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db import get_db
from app.models.llm_provider import LLMProviderConfig
from app.schemas.llm_provider import LLMProviderCreate, LLMProviderResponse

router = APIRouter(prefix="/llm-providers", tags=["llm-providers"])


def _fernet() -> Fernet:
    key = os.getenv("SECRET_KEY")
    if not key:
        raise HTTPException(status_code=500, detail="SECRET_KEY is not configured")
    return Fernet(key.encode())


@router.get("", response_model=list[LLMProviderResponse])
def list_providers(db: Session = Depends(get_db)):
    return db.query(LLMProviderConfig).order_by(LLMProviderConfig.created_at.desc()).all()


@router.get("/{provider_id}", response_model=LLMProviderResponse)
def get_provider(provider_id: str, db: Session = Depends(get_db)):
    cfg = db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")
    return cfg


@router.post("", response_model=LLMProviderResponse, status_code=201)
def create_provider(body: LLMProviderCreate, db: Session = Depends(get_db)):
    encrypted_key = None
    if body.api_key:
        encrypted_key = _fernet().encrypt(body.api_key.encode()).decode()

    cfg = LLMProviderConfig(
        name=body.name,
        provider=body.provider,
        model=body.model,
        api_key_encrypted=encrypted_key,
        base_url=body.base_url,
    )
    db.add(cfg)
    db.commit()
    db.refresh(cfg)
    return cfg


@router.patch("/{provider_id}/activate", response_model=LLMProviderResponse)
def activate_provider(provider_id: str, db: Session = Depends(get_db)):
    cfg = db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")

    # deactivate all others, then activate this one
    db.query(LLMProviderConfig).update({LLMProviderConfig.is_active: False})
    cfg.is_active = True
    db.commit()
    db.refresh(cfg)
    return cfg


@router.delete("/{provider_id}", status_code=204)
def delete_provider(provider_id: str, db: Session = Depends(get_db)):
    cfg = db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")
    db.delete(cfg)
    db.commit()
