from fastapi import HTTPException
import enum
import os
from pydantic import BaseModel
from app.models.llm_provider import LLMProviderConfig
from app.services.crypto import get_fernet


from langchain_openai import ChatOpenAI
from langchain_anthropic import ChatAnthropic
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_ollama import ChatOllama


class RuntimeProvider(BaseModel):
    id: str
    provider: str
    model: str
    api_key: str | None
    base_url: str | None


def decrypt_api_key(encrypted: str | None) -> str | None:
    if not encrypted:
        return None
    return get_fernet().decrypt(encrypted.encode()).decode()


def resolve_runtime_provider(cfg: LLMProviderConfig) -> RuntimeProvider:
    return RuntimeProvider(
        id=cfg.id,
        provider=cfg.provider,
        model=cfg.model,
        api_key=decrypt_api_key(cfg.api_key_encrypted),
        base_url=cfg.base_url,
    )


ALLOWED_PROVIDERS = {"openai", "openrouter", "anthropic", "gemini", "ollama", "custom"}


def validate_provider(cfg: LLMProviderConfig):
    if normalize_provider(cfg) not in ALLOWED_PROVIDERS:
        raise ValueError(f"Unsupported provider: {cfg.provider}")


def normalize_provider(cfg: LLMProviderConfig) -> str:
    if isinstance(cfg.provider, enum.Enum):
        return cfg.provider.value
    return str(cfg.provider).lower()


def build_llm(runtime):
    if runtime.provider == "ollama":
        return ChatOllama(
            model=runtime.model,
            base_url=runtime.base_url or "http://localhost:11434",
            temperature=0,
        )

    if not runtime.api_key:
        return ChatOpenAI(
            model="gpt-4o-mini",
            api_key=os.getenv("OPENAI_FALLBACK_KEY"),
        )

    if runtime.provider in ["openai", "openrouter"]:
        return ChatOpenAI(
            model=runtime.model,
            api_key=runtime.api_key,
            base_url=runtime.base_url if runtime.provider == "openrouter" else None,
            temperature=0,
        )

    if runtime.provider == "anthropic":
        return ChatAnthropic(
            model=runtime.model,
            api_key=runtime.api_key,
            temperature=0,
        )

    if runtime.provider == "gemini":
        return ChatGoogleGenerativeAI(
            model=runtime.model,
            google_api_key=runtime.api_key,
            temperature=0,
        )

    if runtime.provider == "custom":
        return ChatOpenAI(
            model=runtime.model,
            api_key=runtime.api_key,
            base_url=runtime.base_url,
            temperature=0,
        )

    raise HTTPException(
        status_code=400,
        detail=f"Unsupported provider: {runtime.provider}",
    )
