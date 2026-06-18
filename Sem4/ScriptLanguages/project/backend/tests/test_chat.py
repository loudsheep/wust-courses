import json
from unittest.mock import patch
from fastapi.testclient import TestClient

def test_chat_no_active_provider(client: TestClient):
    response = client.post("/api/v1/chat", json={"message": "hello"})
    assert response.status_code == 400
    assert "No active provider configured" in response.json()["detail"]

@patch("app.services.chat.run_agent")
def test_chat_success(mock_run_agent, client: TestClient):
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    with patch("app.api.llm_providers.test_provider_connection") as mock_conn:
        mock_conn.return_value = (True, "OK")
        resp = client.get("/api/v1/llm-providers")
        p_id = resp.json()[0]["id"]
        client.post(f"/api/v1/llm-providers/{p_id}/toggle-active")

    async def mock_agent(*args, **kwargs):
        yield {"type": "content", "text": "Hello "}
        yield {"type": "content", "text": "from AI"}
        yield {"type": "retrieved_chunks", "chunks": []}
        yield {"type": "suggestion_chips", "chips": ["Tell me more", "Any examples?"]}

    mock_run_agent.side_effect = mock_agent

    response = client.post("/api/v1/chat", json={"message": "hello"})
    assert response.status_code == 200
    assert response.headers["content-type"] == "text/event-stream; charset=utf-8"

    events = []
    for line in response.iter_lines():
        if isinstance(line, bytes):
            line = line.decode("utf-8")
        if line.startswith("data: "):
            data = line[6:].strip()
            if data == "[DONE]":
                continue
            if data:
                events.append(json.loads(data))

    assert "conversation_id" in events[0]
    assert events[1]["content"] == "Hello "
    assert events[2]["content"] == "from AI"
    components = events[-1]["components"]
    assert any(c["type"] == "suggestion_chips" for c in components)
    chips = next(c for c in components if c["type"] == "suggestion_chips")
    assert chips["chips"] == ["Tell me more", "Any examples?"]

@patch("app.services.chat.run_agent")
def test_chat_with_components(mock_run_agent, client: TestClient):
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    with patch("app.api.llm_providers.test_provider_connection") as mock_conn:
        mock_conn.return_value = (True, "OK")
        p_id = client.get("/api/v1/llm-providers").json()[0]["id"]
        client.post(f"/api/v1/llm-providers/{p_id}/toggle-active")

    def parse_events(response):
        events = []
        for line in response.iter_lines():
            if isinstance(line, bytes):
                line = line.decode("utf-8")
            if line.startswith("data: ") and not line.endswith("[DONE]"):
                data = line[6:].strip()
                if data:
                    events.append(json.loads(data))
        return events

    async def mock_agent_with_chunks(*args, **kwargs):
        yield {"type": "content", "text": "Searching..."}
        yield {
            "type": "retrieved_chunks",
            "chunks": [
                {
                    "document_id": "doc-1",
                    "document_name": "doc1.txt",
                    "chunk_index": 0,
                    "text": "Some retrieved text",
                    "score": 0.9,
                }
            ],
        }

    mock_run_agent.side_effect = mock_agent_with_chunks
    events = parse_events(client.post("/api/v1/chat", json={"message": "search in docs"}))
    components = events[-1]["components"]
    assert any(c["type"] == "retrieval_panel" for c in components)

    async def mock_agent_with_chips(*args, **kwargs):
        yield {"type": "content", "text": "Here is my answer."}
        yield {"type": "retrieved_chunks", "chunks": []}
        yield {"type": "suggestion_chips", "chips": ["Follow up 1", "Follow up 2"]}

    mock_run_agent.side_effect = mock_agent_with_chips
    events = parse_events(client.post("/api/v1/chat", json={"message": "tell me about X"}))
    components = events[-1]["components"]
    assert any(c["type"] == "suggestion_chips" for c in components)
    chips = next(c for c in components if c["type"] == "suggestion_chips")
    assert chips["chips"] == ["Follow up 1", "Follow up 2"]

    async def mock_agent_plain(*args, **kwargs):
        yield {"type": "content", "text": "Plain answer."}
        yield {"type": "retrieved_chunks", "chunks": []}

    mock_run_agent.side_effect = mock_agent_plain
    events = parse_events(client.post("/api/v1/chat", json={"message": "hello"}))
    components = events[-1]["components"]
    assert components == []

@patch("app.services.chat.run_agent")
def test_chat_tool_call_event(mock_run_agent, client: TestClient):
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    with patch("app.api.llm_providers.test_provider_connection") as mock_conn:
        mock_conn.return_value = (True, "OK")
        p_id = client.get("/api/v1/llm-providers").json()[0]["id"]
        client.post(f"/api/v1/llm-providers/{p_id}/toggle-active")

    async def mock_agent(*args, **kwargs):
        yield {
            "type": "tool_call", "id": "call_1", "tool": "search_documents",
            "status": "running", "args": {"query": "refunds", "k": 4},
        }
        yield {
            "type": "tool_call", "id": "call_1", "tool": "search_documents",
            "status": "done", "args": {"query": "refunds", "k": 4},
            "result_summary": "Found 1 chunk(s)",
        }
        yield {"type": "content", "text": "Based on your documents..."}
        yield {"type": "retrieved_chunks", "chunks": []}

    mock_run_agent.side_effect = mock_agent

    response = client.post("/api/v1/chat", json={"message": "what is the refund policy?"})
    events = []
    for line in response.iter_lines():
        if isinstance(line, bytes):
            line = line.decode("utf-8")
        if line.startswith("data: "):
            data = line[6:].strip()
            if data and data != "[DONE]":
                events.append(json.loads(data))

    tool_events = [e for e in events if "tool_call" in e]
    assert len(tool_events) == 2
    assert tool_events[0]["tool_call"]["status"] == "running"
    assert tool_events[1]["tool_call"]["status"] == "done"
    assert tool_events[1]["tool_call"]["result_summary"] == "Found 1 chunk(s)"

    final_components = events[-1]["components"]
    assert any(c.get("type") == "tool_call" and c.get("status") == "done" for c in final_components)
