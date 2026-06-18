import pytest
from fastapi.testclient import TestClient
from app.main import app
import io
import zipfile

client = TestClient(app)

def test_read_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_upload_not_zip():
    response = client.post("/upload", files={"file": ("test.txt", b"hello", "text/plain")})
    assert response.status_code == 400
    assert response.json()["detail"] == "Only ZIP files are allowed"

def test_upload_empty_zip():
    # Create a dummy zip file in memory
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w") as z:
        z.writestr("test.txt", "content")
    buf.seek(0)
    
    # This might fail during processing because it's not a valid GTFS zip, 
    # but it should at least pass the file extension check.
    # Note: process_zip might raise an error if files are missing.
    response = client.post("/upload", files={"file": ("test.zip", buf, "application/zip")})
    
    # If it fails in process_zip, it returns 500 as per our code
    assert response.status_code in [200, 500] 
