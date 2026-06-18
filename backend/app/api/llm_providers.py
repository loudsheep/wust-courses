import os
import traceback

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db import get_db
from app.models.llm_provider import LLMProviderConfig
from app.schemas.llm_provider import LLMProviderCreate, LLMProviderResponse
from app.services.crypto import get_fernet

from app.services.provider_resolver import (
    resolve_runtime_provider,
    validate_provider,
    build_llm,
)

router = APIRouter(prefix="/llm-providers", tags=["llm-providers"])


class ActivateProviderResponse(BaseModel):
    success: bool
    message: str


def test_provider_connection(runtime) -> tuple[bool, str]:
    try:
        llm = build_llm(runtime)
        llm.invoke("Reply with OK")
        return True, "Connection successful"

    except Exception as e:
        msg = str(e)

        # lightweight normalization (optional but useful)
        if "401" in msg or "invalid" in msg.lower():
            return False, "Invalid API key"

        if "404" in msg or "not found" in msg.lower():
            return False, "Model not found"

        if "connection" in msg.lower() or "refused" in msg.lower():
            return False, "Cannot connect to provider"

        return False, msg


@router.get("", response_model=list[LLMProviderResponse])
def list_providers(active_only: bool = False, db: Session = Depends(get_db)):
    query = db.query(LLMProviderConfig)
    if active_only:
        query = query.filter(LLMProviderConfig.is_active == True)
    return query.order_by(LLMProviderConfig.created_at.desc()).all()


@router.get("/{provider_id}", response_model=LLMProviderResponse)
def get_provider_endpoint(provider_id: str, db: Session = Depends(get_db)):
    cfg = (
        db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    )

    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")

    return cfg


@router.post("", response_model=LLMProviderResponse, status_code=201)
def create_provider(body: LLMProviderCreate, db: Session = Depends(get_db)):
    try:
        encrypted_key = None

        if body.api_key:
            encrypted_key = get_fernet().encrypt(body.api_key.encode()).decode()

        cfg = LLMProviderConfig(
            name=body.name,
            provider=body.provider,
            model=body.model,
            api_key_encrypted=encrypted_key,
            base_url=body.base_url,
            is_active=False,
        )

        db.add(cfg)
        db.commit()
        db.refresh(cfg)

        return cfg

    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{provider_id}", status_code=204)
def delete_provider(provider_id: str, db: Session = Depends(get_db)):
    cfg = (
        db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    )

    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")

    db.delete(cfg)
    db.commit()


@router.post(
    "/{provider_id}/toggle-active",
    response_model=ActivateProviderResponse,
)
def toggle_provider_active(provider_id: str, db: Session = Depends(get_db)):
    cfg = (
        db.query(LLMProviderConfig).filter(LLMProviderConfig.id == provider_id).first()
    )

    if not cfg:
        raise HTTPException(status_code=404, detail="Provider not found")

    try:
        if not cfg.is_active:
            validate_provider(cfg)
            runtime = resolve_runtime_provider(cfg)
            success, message = test_provider_connection(runtime)

            if not success:
                return ActivateProviderResponse(
                    success=False,
                    message=message,
                )

        cfg.is_active = not cfg.is_active

        db.commit()
        db.refresh(cfg)

        return ActivateProviderResponse(
            success=True,
            message="Provider status toggled successfully",
        )

    except Exception as e:
        traceback.print_exc()
        raise HTTPException(
            status_code=500,
            detail=str(e),
        )

