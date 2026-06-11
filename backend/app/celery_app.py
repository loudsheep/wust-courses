import os

from celery import Celery

REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")

celery_app = Celery(
    "rag_document_insights",
    broker=REDIS_URL,
    include=["app.tasks.documents"],
)

celery_app.conf.update(
    task_serializer="json",
    accept_content=["json"],
    task_track_started=True,
)
