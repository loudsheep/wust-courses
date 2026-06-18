# Architecture

## Layers

```
frontend/   React UI — talks to the API only, no business logic
backend/
  app/api/       route handlers — thin, validate input, call services
  app/services/  business logic (agent, tools, chat, chunking, rag, vector_store,
                 embeddings, document_extraction, crypto, provider_resolver)
  app/models/    SQLAlchemy ORM models
  app/schemas/   Pydantic request/response models (kept separate from ORM models)
  app/tasks/     Celery background jobs
  alembic/       DB migrations
```

Routes never touch the database or external APIs directly — they call into
`app/services/` and return Pydantic schemas, never raw ORM objects.

## Data stores

| Store | Holds |
|---|---|
| SQLite | documents, conversations, messages, LLM provider configs |
| ChromaDB | chunk embeddings (vector search) |
| Redis | Celery task queue |

## Document lifecycle

1. `POST /api/v1/documents` — file is saved to disk, content-type validated by magic
   bytes (`app/services/document_extraction.detect_mime_type`), a `Document` row is
   created with status `pending`, and a Celery task is queued.
2. `app/tasks/documents.py` extracts text, splits it into chunks
   (`app/services/chunking.py`, using `DEFAULT_CHUNK_SIZE`/`DEFAULT_CHUNK_OVERLAP`
   env vars — there's no per-document override or settings UI), embeds them
   (OpenAI), and writes them to ChromaDB. `Document.status` moves to `indexing` then
   `indexed` (or `failed` with `error_message` set). The chunk size/overlap used are
   recorded on the row afterward, for reference.
3. `Document.is_stale` exists for a planned "settings changed, please reindex" flow
   and is checked by the bulk reindex endpoint, but nothing currently sets it to
   `True` — reindexing today is always a manual, explicit action per document.

## Chat request lifecycle

```
POST /api/v1/chat
  -> resolve LLM provider (app/services/provider_resolver.py)
  -> chat.stream_chat:
       - create/load Conversation, save the user Message
       - load conversation history, trim to CHAT_HISTORY_LIMIT
       - run agent.run_agent (tool-calling loop, see below)
       - persist the assistant Message with its components
       - stream SSE lines to the client
```

### SSE protocol

Raw `data: {...}` JSON lines (not named `event:` types):

1. `{"conversation_id": "..."}` — first line, lets a new chat learn its ID
2. `{"content": "..."}` — zero or more streamed text chunks
3. `{"tool_call": {...}}` — zero or more, sent twice per tool call (`status:
   "running"` then `"done"`/`"error"`, same `id` both times)
4. `{"components": [...]}` — final line, structured UI payload (see below)
5. `data: [DONE]` — terminator

### Agent loop (`app/services/agent.run_agent`)

A manual tool-calling loop, not LangChain's `AgentExecutor` (that doesn't stream
custom events well). Up to 5 iterations: stream the LLM's response, and if it
requested tools, run them and feed the results back as `ToolMessage`s before looping.

Tools available to the agent (`app/services/tools.build_tools`):

- `search_documents` — semantic search across all documents
- `keyword_search` — exact-match search, for names/numbers/dates semantic search
  might miss
- `search_documents_filtered` — semantic search scoped to one document
- `list_documents` — what's indexed and available
- `get_document_chunk` — full text of a chunk plus neighbors, for truncated excerpts
- `get_document_metadata` — filename, size, type, upload date for one document
- `suggest_followups` — records follow-up question suggestions for the UI

### Structured UI components

The frontend renders the final `components` array by `type`:

- `retrieval_panel` — excerpts gathered by any retrieval tool during the turn
- `suggestion_chips` — follow-up questions from `suggest_followups`
- `tool_call` — persisted record of each finished tool call, so it survives a
  page reload

## Deployment

`docker-compose.yml` runs five services: `backend` (FastAPI), `celery_worker`,
`redis`, `chromadb`, and `nginx`. The `nginx` image builds the React app from
source (`nginx/Dockerfile` has a `node` build stage) and serves the static output
itself, while also reverse-proxying to `backend` and enforcing HTTP Basic Auth via
`nginx/.htpasswd`. There's no separate frontend container. See the README for the
full setup (`.env`, `nginx/.htpasswd`, `docker compose up --build`).
