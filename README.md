# RAG Document Insights

Self-hosted RAG chat app — upload documents, ask questions,
get answers grounded in your files.

## Docs

- [docs/user-guide.md](docs/user-guide.md) — how to use the app
- [docs/architecture.md](docs/architecture.md) — technical overview, modules, data flow
- [docs/testing.md](docs/testing.md) — how to run the test suite

## Features

- Upload PDF, DOCX, TXT, MD — content-type checked by file signature, not extension
- Background indexing (Celery) into ChromaDB, with live status per document
- Chat with a tool-calling agent that decides when to search your documents
  (semantic search, keyword search, filtered search, chunk lookup)
- Follow-up question suggestions after document-grounded answers
- Multiple LLM providers, switchable per chat (OpenAI-compatible APIs)
- Chunk size/overlap recorded per document (set via `DEFAULT_CHUNK_SIZE`/
  `DEFAULT_CHUNK_OVERLAP` env vars — no settings UI for this yet)
- Encrypted provider API keys at rest (Fernet)

## Stack

| Layer            | Tech                                   |
| ---------------- | -------------------------------------- |
| API              | FastAPI + SQLite                       |
| Background tasks | Celery + Redis                         |
| Vector store     | ChromaDB                               |
| Embeddings       | OpenAI API (text-embedding-3-small)    |
| Frontend         | React 19 + Vite + Tailwind + shadcn/ui |

## Local dev

### Backend

```bash
cd backend
python -m venv venv && venv\Scripts\activate   # Windows
# source venv/bin/activate                      # macOS/Linux

pip install -r requirements.txt

cp ../.env.example ../.env
# edit .env — generate SECRET_KEY with:
# python -c "from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())"

alembic upgrade head       # creates data/app.db
python app/main.py             # runs on http://localhost:8000
```

Swagger UI: `http://localhost:8000/docs`

### Frontend

```bash
cd frontend
npm install

cp .env.example .env      # VITE_API_URL=http://localhost:8000

npm run dev               # runs on http://localhost:5173
```

Both must be running at the same time.

## Run with Docker

```bash
cp .env.example .env
# edit .env — generate SECRET_KEY with:
# python -c "from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())"
```

### Basic Auth

The whole app (frontend + API) is protected by HTTP Basic Auth via nginx

Generate `nginx/.htpasswd` (gitignored, never commit it):

```powershell
# PowerShell
$hash = docker run --rm httpd:alpine htpasswd -nbm youruser yourpassword
[System.IO.File]::WriteAllText("nginx\.htpasswd", "$hash`n", [System.Text.UTF8Encoding]::new($false))
```

```bash
# macOS/Linux
docker run --rm httpd:alpine htpasswd -nbm youruser yourpassword > nginx/.htpasswd
```

```bash
docker compose up --build
```

App: `http://localhost`

## Migrations

```bash
# from backend/
alembic upgrade head                              # apply all
alembic revision --autogenerate -m "description"  # generate after model changes
alembic downgrade -1                              # roll back one
```

## Tests

```bash
cd backend
pytest
```

See [docs/testing.md](docs/testing.md) for what's covered and how to write new tests.
