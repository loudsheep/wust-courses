import os

from openai import OpenAI

EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL", "text-embedding-3-small")

_client: OpenAI | None = None


def get_embedding_client() -> OpenAI:
    global _client
    if _client is None:
        api_key = os.getenv("OPENAI_EMBEDDING_API_KEY")
        if not api_key:
            raise RuntimeError(
                "OPENAI_EMBEDDING_API_KEY is not set. Configure it to enable embeddings."
            )
        _client = OpenAI(api_key=api_key)
    return _client


def embed_texts(texts: list[str]) -> list[list[float]]:
    if not texts:
        return []
    client = get_embedding_client()
    response = client.embeddings.create(model=EMBEDDING_MODEL_NAME, input=texts)
    return [item.embedding for item in response.data]
