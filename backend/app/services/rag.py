def run_rag(message: str) -> str | None:
    if "doc" in message.lower():
        return "Retrieved context from vector DB"
    return None
