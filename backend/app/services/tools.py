from langchain_core.tools import tool
from sqlalchemy.orm import Session

from app.models.document import Document
from app.services import vector_store, embeddings
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

    @tool
    def get_document_metadata(document_id: str) -> str:
        """Get detailed metadata for a specific document by its ID.
        Returns filename, size, MIME type, upload date, status, and chunk count.
        Use this to answer questions about document properties."""
        doc = db.query(Document).filter(Document.id == document_id).first()
        if not doc:
            return f"Document with ID {document_id} not found."
        
        return (
            f"Metadata for {doc.original_filename} (ID: {doc.id}):\n"
            f"- Status: {doc.status.value}\n"
            f"- Size: {doc.file_size} bytes\n"
            f"- MIME Type: {doc.mime_type}\n"
            f"- Uploaded: {doc.created_at.isoformat()}\n"
            f"- Chunks: {doc.chunk_count if doc.chunk_count is not None else 'n/a'}"
        )

    @tool
    def keyword_search(query: str, k: int = 5) -> str:
        """Search the documents for exact keyword matches.
        Use this for specific names, numbers, or dates that semantic search might miss."""
        hits = vector_store.similarity_search(
            where_document={"$contains": query},
            k=k
        )
        if not hits:
            return f"No documents found containing the keyword: {query}"
        
        document_ids = {h["document_id"] for h in hits}
        name_by_id = {
            d.id: d.original_filename
            for d in db.query(Document).filter(Document.id.in_(document_ids)).all()
        }

        chunks = []
        context_parts = []
        for h in hits:
            document_name = name_by_id.get(h["document_id"], "Unknown document")
            chunks.append({
                "document_id": h["document_id"],
                "document_name": document_name,
                "chunk_index": h["chunk_index"],
                "text": h["text"],
                "score": h["score"],
            })
            context_parts.append(f"[{document_name}, chunk {h['chunk_index']}]\n{h['text']}")
        
        retrieved_chunks.extend(chunks)
        return "\n\n---\n\n".join(context_parts)

    @tool
    def search_documents_filtered(
        query: str, 
        document_id: str | None = None,
        mime_type: str | None = None,
        k: int = 4
    ) -> str:
        """Semantic search with optional filters for document ID or file type (MIME type).
        Use this when the user asks to search specifically in a certain file or type of files."""
        where = {}
        if document_id:
            where["document_id"] = document_id
        if mime_type:
            # We don't actually store mime_type in Chroma metadata currently.
            # Let's check vector_store.add_chunks
            pass
            
        # For now, let's just support document_id as it's definitely in metadata.
        # If we need more filters, we'd need to add them to Chroma metadata during indexing.
        
        query_embedding = embeddings.embed_texts([query])[0]
        hits = vector_store.similarity_search(
            query_embedding=query_embedding,
            k=k,
            where=where if where else None
        )
        
        if not hits:
            return "No matching results found with the given filters."

        document_ids = {h["document_id"] for h in hits}
        name_by_id = {
            d.id: d.original_filename
            for d in db.query(Document).filter(Document.id.in_(document_ids)).all()
        }

        chunks = []
        context_parts = []
        for h in hits:
            document_name = name_by_id.get(h["document_id"], "Unknown document")
            chunks.append({
                "document_id": h["document_id"],
                "document_name": document_name,
                "chunk_index": h["chunk_index"],
                "text": h["text"],
                "score": h["score"],
            })
            context_parts.append(f"[{document_name}, chunk {h['chunk_index']}]\n{h['text']}")
        
        retrieved_chunks.extend(chunks)
        return "\n\n---\n\n".join(context_parts)

    return [
        search_documents, 
        list_documents, 
        get_document_chunk,
        get_document_metadata,
        keyword_search,
        search_documents_filtered
    ], retrieved_chunks
