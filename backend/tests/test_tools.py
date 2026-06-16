import pytest
from unittest.mock import MagicMock, patch
from sqlalchemy.orm import Session
from app.models.document import Document, DocumentStatus
from app.services.tools import build_tools
from datetime import datetime

def test_get_document_metadata(db_session: Session):
    # Setup
    doc = Document(
        id="test-doc-id",
        original_filename="test_file.pdf",
        stored_filename="stored_file.pdf",
        file_path="/tmp/test_file.pdf",
        file_size=1024,
        mime_type="application/pdf",
        status=DocumentStatus.INDEXED,
        chunk_count=10,
        created_at=datetime(2023, 1, 1)
    )
    db_session.add(doc)
    db_session.commit()

    tools, _ = build_tools(db_session)
    get_metadata_tool = next(t for t in tools if t.name == "get_document_metadata")

    # Execute
    result = get_metadata_tool.invoke({"document_id": "test-doc-id"})

    # Assert
    assert "test_file.pdf" in result
    assert "1024 bytes" in result
    assert "application/pdf" in result
    assert "indexed" in result
    assert "10" in result

def test_get_document_metadata_not_found(db_session: Session):
    tools, _ = build_tools(db_session)
    get_metadata_tool = next(t for t in tools if t.name == "get_document_metadata")

    result = get_metadata_tool.invoke({"document_id": "non-existent"})
    assert "not found" in result

@patch("app.services.vector_store.similarity_search")
def test_keyword_search(mock_similarity_search, db_session: Session):
    # Setup
    doc = Document(id="doc1", original_filename="doc1.txt", stored_filename="s1", file_path="p1", file_size=10, mime_type="text/plain")
    db_session.add(doc)
    db_session.commit()

    mock_similarity_search.return_value = [
        {"document_id": "doc1", "chunk_index": 0, "text": "This is a keyword match", "score": 1.0}
    ]

    tools, retrieved_chunks = build_tools(db_session)
    keyword_search_tool = next(t for t in tools if t.name == "keyword_search")

    # Execute
    result = keyword_search_tool.invoke({"query": "keyword"})

    # Assert
    assert "doc1.txt" in result
    assert "chunk 0" in result
    assert "This is a keyword match" in result
    assert len(retrieved_chunks) == 1
    assert retrieved_chunks[0]["document_id"] == "doc1"
    mock_similarity_search.assert_called_once()
    args, kwargs = mock_similarity_search.call_args
    assert kwargs["where_document"] == {"$contains": "keyword"}

@patch("app.services.embeddings.embed_texts")
@patch("app.services.vector_store.similarity_search")
def test_search_documents_filtered(mock_similarity_search, mock_embed, db_session: Session):
    # Setup
    doc = Document(id="doc1", original_filename="doc1.txt", stored_filename="s1", file_path="p1", file_size=10, mime_type="text/plain")
    db_session.add(doc)
    db_session.commit()

    mock_embed.return_value = [[0.1, 0.2, 0.3]]
    mock_similarity_search.return_value = [
        {"document_id": "doc1", "chunk_index": 5, "text": "Filtered result", "score": 0.8}
    ]

    tools, retrieved_chunks = build_tools(db_session)
    filtered_search_tool = next(t for t in tools if t.name == "search_documents_filtered")

    # Execute
    result = filtered_search_tool.invoke({"query": "hello", "document_id": "doc1"})

    # Assert
    assert "doc1.txt" in result
    assert "chunk 5" in result
    assert "Filtered result" in result
    assert len(retrieved_chunks) == 1
    mock_similarity_search.assert_called_once()
    args, kwargs = mock_similarity_search.call_args
    assert kwargs["where"] == {"document_id": "doc1"}
