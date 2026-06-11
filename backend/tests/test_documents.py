import io
import os
from fastapi.testclient import TestClient


def test_upload_document(client: TestClient):
    file_content = b"This is a test document content."
    file_name = "test_doc.txt"
    file = (file_name, io.BytesIO(file_content), "text/plain")

    response = client.post("/api/v1/documents", files={"file": file})

    assert response.status_code == 202

    data = response.json()
    assert data["original_filename"] == file_name
    assert data["mime_type"] == "text/plain"
    assert "id" in data
    assert "file_path" in data

    file_path = data["file_path"]
    assert os.path.exists(file_path)

    if os.path.exists(file_path):
        os.remove(file_path)


def test_list_documents(client: TestClient):
    client.post(
        "/api/v1/documents",
        files={"file": ("doc1.txt", io.BytesIO(b"content1"), "text/plain")},
    )
    client.post(
        "/api/v1/documents",
        files={"file": ("doc2.txt", io.BytesIO(b"content2"), "text/plain")},
    )

    response = client.get("/api/v1/documents")

    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2
    assert data[0]["original_filename"] in ["doc1.txt", "doc2.txt"]
    assert data[1]["original_filename"] in ["doc1.txt", "doc2.txt"]

    for doc in data:
        if os.path.exists(doc["file_path"]):
            os.remove(doc["file_path"])


def test_get_document(client: TestClient):
    upload_response = client.post(
        "/api/v1/documents",
        files={"file": ("test.txt", io.BytesIO(b"test content"), "text/plain")},
    )
    doc_id = upload_response.json()["id"]

    response = client.get(f"/api/v1/documents/{doc_id}")

    assert response.status_code == 200
    assert response.json()["id"] == doc_id

    if os.path.exists(upload_response.json()["file_path"]):
        os.remove(upload_response.json()["file_path"])


def test_delete_document(client: TestClient):
    upload_response = client.post(
        "/api/v1/documents",
        files={"file": ("to_delete.txt", io.BytesIO(b"delete me"), "text/plain")},
    )
    doc_id = upload_response.json()["id"]
    file_path = upload_response.json()["file_path"]

    response = client.delete(f"/api/v1/documents/{doc_id}")

    assert response.status_code == 204

    get_response = client.get(f"/api/v1/documents/{doc_id}")
    assert get_response.status_code == 404

    assert not os.path.exists(file_path)


def test_get_nonexistent_document(client: TestClient):
    response = client.get("/api/v1/documents/non-existent-id")
    assert response.status_code == 404
    assert response.json()["detail"] == "Document not found"


def test_delete_nonexistent_document(client: TestClient):
    response = client.delete("/api/v1/documents/non-existent-id")
    assert response.status_code == 404
    assert response.json()["detail"] == "Document not found"
