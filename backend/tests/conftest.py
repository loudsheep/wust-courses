import os
import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.main import app
from app.db import get_db
from app.db.base import Base
from app.tasks.documents import delete_document_task
from cryptography.fernet import Fernet
import app.db.session as db_session_module

# Use in-memory SQLite for testing
SQLALCHEMY_DATABASE_URL = "sqlite://"

# Set a default SECRET_KEY for testing if not already set
if "SECRET_KEY" not in os.environ:
    os.environ["SECRET_KEY"] = Fernet.generate_key().decode()

engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    connect_args={"check_same_thread": False},
    poolclass=StaticPool,
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


@pytest.fixture(scope="function")
def db_session():
    # Create the database and tables
    Base.metadata.create_all(bind=engine)
    session = TestingSessionLocal()
    try:
        yield session
    finally:
        session.close()
        # Drop the tables after the test
        Base.metadata.drop_all(bind=engine)


@pytest.fixture(scope="function")
def client(db_session, monkeypatch):
    def override_get_db():
        try:
            yield db_session
        finally:
            pass

    app.dependency_overrides[get_db] = override_get_db

    monkeypatch.setattr(db_session_module, "SessionLocal", TestingSessionLocal)

    monkeypatch.setattr("app.api.documents.index_document_task.delay", lambda *a, **kw: None)
    monkeypatch.setattr("app.api.documents.reindex_document_task.delay", lambda *a, **kw: None)
    monkeypatch.setattr(
        "app.api.documents.delete_document_task.delay",
        lambda document_id: delete_document_task.run(document_id),
    )

    with TestClient(app) as test_client:
        yield test_client
    app.dependency_overrides.clear()
