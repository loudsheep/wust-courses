# Testing

```bash
cd backend
pytest          # all tests
pytest -v       # verbose
pytest tests/test_documents.py   # single file
```

Tests use a real SQLite database (via `conftest.py` fixtures), not a mocked DB layer
— only external calls (LLM providers, embeddings, ChromaDB similarity search) are
mocked, since hitting real APIs in CI is neither free nor deterministic.

## What's covered

| File | Covers |
|---|---|
| `test_documents.py` | upload, list, get, delete documents; 404s for missing IDs |
| `test_llm_providers.py` | provider CRUD, activate/deactivate with connection check |
| `test_chat_providers.py` | provider-specific runtime building/validation (Gemini, Ollama) |
| `test_chat.py` | chat endpoint: no active provider, success path, components in
  response, tool_call SSE events |
| `test_chat_history.py` | conversation lifecycle (create/list/delete), provider
  listing/filtering |
| `test_tools.py` | agent tools — `get_document_metadata`, `keyword_search`,
  `search_documents_filtered` |
| `test_crypto.py` | Fernet key loading, missing-key error case |
| `test_llm.py` | `extract_text_content` against the various message content shapes
  LangChain can return |

## Adding a test

- API-level behavior → add to the matching `test_*.py` next to the route file it
  covers, using the `client` fixture from `conftest.py`.
- Service/tool logic → use the `db_session` fixture directly, mock only the
  external boundary (embeddings, vector store, LLM calls) with `unittest.mock.patch`.
- Edge cases worth a test: missing/invalid IDs, empty input, provider connection
  failures — not full UI flows (no frontend tests planned for this project).
