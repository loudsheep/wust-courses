from app.services.provider_resolver import (
    validate_provider,
    LLMProviderConfig,
    build_llm,
    RuntimeProvider,
)
from app.models.llm_provider import LLMProvider


def test_validate_provider_gemini():
    cfg = LLMProviderConfig(provider=LLMProvider.GEMINI)
    # This should not raise ValueError
    validate_provider(cfg)


def test_validate_provider_ollama():
    cfg = LLMProviderConfig(provider=LLMProvider.OLLAMA)
    validate_provider(cfg)


def test_build_llm_gemini_logic():
    runtime = RuntimeProvider(
        id="test-id",
        provider="gemini",
        model="gemini-pro",
        api_key="fake-key",
        base_url=None,
    )
    llm = build_llm(runtime)
    from langchain_google_genai import ChatGoogleGenerativeAI

    assert isinstance(llm, ChatGoogleGenerativeAI)
    assert llm.model == "models/gemini-pro"


def test_build_llm_ollama_logic():
    runtime = RuntimeProvider(
        id="test-id",
        provider="ollama",
        model="llama3",
        api_key=None,
        base_url="http://localhost:11434",
    )
    llm = build_llm(runtime)
    from langchain_ollama import ChatOllama

    assert isinstance(llm, ChatOllama)
    assert llm.model == "llama3"
    assert llm.base_url == "http://localhost:11434"
