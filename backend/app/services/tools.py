from langchain_core.tools import tool
from sqlalchemy.orm import Session

from app.models.document import Document
from app.services import vector_store
from app.services.rag import run_rag


def build_tools(db: Session):
    retrieved_chunks: list[dict] = []

    @tool
    def search_documents(query: str, k: int = 4) -> str:
        """Search the uploaded documents for content relevant to query.
        Returns up to k matching excerpts with their source document and chunk
        index. Use this when the user question might be answered by their
        documents."""
        result = run_rag(query, db, k=k)
        if result is None:
            return "No matching chunks found."
        context_string, chunks = result
        retrieved_chunks.extend(chunks)
        return context_string

    @tool
    def list_documents() -> str:
        """List all documents currently indexed in the knowledge base, with their
        filename, status, and chunk count. Use this to see what is available
        before deciding whether or how to search."""
        docs = (
            db.query(Document)
            .filter(Document.deleted_at.is_(None))
            .order_by(Document.created_at.desc())
            .all()
        )
        if not docs:
            return "No documents have been uploaded yet."
        lines = [
            f"- {d.original_filename} (id={d.id}, status={d.status.value}, "
            f"chunks={d.chunk_count if d.chunk_count is not None else 'n/a'})"
            for d in docs
        ]
        return "\n".join(lines)

    @tool
    def get_document_chunk(document_id: str, chunk_index: int) -> str:
        """Fetch the full text of a specific chunk by document_id and chunk_index,
        plus its immediate neighboring chunks (index - 1 and + 1) for extra
        context. Use this when a search result excerpt is too short or seems cut
        off mid-sentence and more surrounding context is needed."""
        collection = vector_store.get_collection()
        ids = [
            f"{document_id}_{i}"
            for i in (chunk_index - 1, chunk_index, chunk_index + 1)
            if i >= 0
        ]
        results = collection.get(ids=ids)
        found = dict(zip(results["ids"], results["documents"]))
        if f"{document_id}_{chunk_index}" not in found:
            return f"Chunk {chunk_index} not found for document {document_id}."

        doc = db.query(Document).filter(Document.id == document_id).first()
        name = doc.original_filename if doc else "Unknown document"

        parts = []
        for i in (chunk_index - 1, chunk_index, chunk_index + 1):
            key = f"{document_id}_{i}"
            if key in found:
                parts.append(f"[{name}, chunk {i}]\n{found[key]}")
        return "\n\n---\n\n".join(parts)

    return [search_documents, list_documents, get_document_chunk], retrieved_chunks
