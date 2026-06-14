# RAG Document Insights

Self-hosted RAG chat app — upload documents, ask questions,
get answers grounded in your files.

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
