import os

from app.services.chunking import CHUNK_STRATEGY

CHROMA_HOST = os.getenv("CHROMA_HOST", "localhost")
CHROMA_PORT = int(os.getenv("CHROMA_PORT", "8000"))
COLLECTION_NAME = "documents"

_client = None


def get_chroma_client():
    global _client
    if _client is None:
        import chromadb

        if os.getenv("CHROMA_CLIENT_MODE") == "ephemeral":
            _client = chromadb.EphemeralClient()
        else:
            _client = chromadb.HttpClient(host=CHROMA_HOST, port=CHROMA_PORT)
    return _client


def get_collection():
    client = get_chroma_client()
    return client.get_or_create_collection(
        name=COLLECTION_NAME, metadata={"hnsw:space": "cosine"}
    )


def add_chunks(
    document_id: str,
    chunks: list[str],
    embeddings: list[list[float]],
    chunk_size: int,
    chunk_overlap: int,
) -> None:
    if not chunks:
        return

    collection = get_collection()
    ids = [f"{document_id}_{i}" for i in range(len(chunks))]
    metadatas = [
        {
            "document_id": document_id,
            "chunk_index": i,
            "chunk_size_config": chunk_size,
            "chunk_overlap_config": chunk_overlap,
            "strategy": CHUNK_STRATEGY,
        }
        for i in range(len(chunks))
    ]
    collection.add(ids=ids, embeddings=embeddings, documents=chunks, metadatas=metadatas)


def delete_chunks(document_id: str) -> None:
    collection = get_collection()
    collection.delete(where={"document_id": document_id})


def similarity_search(
    query_embedding: list[float] | None = None,
    k: int = 4,
    where: dict | None = None,
    where_document: dict | None = None,
) -> list[dict]:
    collection = get_collection()
    
    query_params = {
        "n_results": k,
        "where": where,
        "where_document": where_document,
    }
    
    if query_embedding:
        query_params["query_embeddings"] = [query_embedding]
        results = collection.query(**query_params)
    else:
        # If no embedding, we just fetch by where/where_document
        # Chroma's .get() doesn't support limit directly in the same way, 
        # but we can use .get() with where filters.
        # However, for keyword search on documents, .query(query_texts) might be better
        # if we want to use Chroma's internal keyword search (if available)
        # but here we use where_document for explicit keyword matching.
        results = collection.get(where=where, where_document=where_document, limit=k)
        # normalize format to match .query()
        results = {
            "ids": [results["ids"]],
            "documents": [results["documents"]],
            "metadatas": [results["metadatas"]],
            "distances": [[0] * len(results["ids"])] if "ids" in results else [[]]
        }

    ids = results.get("ids", [[]])[0]
    if not ids:
        return []

    hits = []
    for doc_text, meta, dist in zip(
        results["documents"][0], results["metadatas"][0], results["distances"][0]
    ):
        hits.append(
            {
                "document_id": meta["document_id"],
                "chunk_index": meta["chunk_index"],
                "text": doc_text,
                "score": round(1 - dist, 3) if dist is not None else 0.0,
            }
        )
    return hits
