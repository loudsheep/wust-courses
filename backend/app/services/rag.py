from sqlalchemy.orm import Session

from app.models.document import Document
from app.services import embeddings, vector_store

DEFAULT_K = 4


def run_rag(message: str, db: Session, k: int = DEFAULT_K) -> tuple[str, list[dict]] | None:
    query_embedding = embeddings.embed_texts([message])[0]
    hits = vector_store.similarity_search(query_embedding, k=k)
    if not hits:
        return None

    document_ids = {h["document_id"] for h in hits}
    name_by_id = {
        d.id: d.original_filename
        for d in db.query(Document).filter(Document.id.in_(document_ids)).all()
    }

    chunks = []
    context_parts = []
    for h in hits:
        document_name = name_by_id.get(h["document_id"], "Unknown document")
        chunks.append(
            {
                "document_id": h["document_id"],
                "document_name": document_name,
                "chunk_index": h["chunk_index"],
                "text": h["text"],
                "score": h["score"],
            }
        )
        context_parts.append(f"[{document_name}, chunk {h['chunk_index']}]\n{h['text']}")

    return "\n\n---\n\n".join(context_parts), chunks
