import json
from unittest.mock import patch, AsyncMock
from fastapi.testclient import TestClient

def test_chat_no_active_provider(client: TestClient):
    response = client.post("/api/v1/chat", json={"message": "hello"})
    assert response.status_code == 400
    assert "No active provider configured" in response.json()["detail"]

def parse_sse(text: str):
    events = []
    for line in text.split("\n"):
        if line.startsWith("data: "):
            data = line[6:].strip()
            if data == "[DONE]":
                continue
            if data:
                events.append(json.loads(data))
    return events

@patch("app.services.chat.run_rag")
@patch("app.services.chat.stream_llm")
def test_chat_success(mock_stream_llm, mock_rag, client: TestClient):
    # Setup active provider
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    with patch("app.api.llm_providers.test_provider_connection") as mock_conn:
        mock_conn.return_value = (True, "OK")
        resp = client.get("/api/v1/llm-providers")
        p_id = resp.json()[0]["id"]
        client.post(f"/api/v1/llm-providers/{p_id}/activate")

    mock_rag.return_value = "Test RAG Context"
    
    async def mock_stream(*args, **kwargs):
        yield "Hello "
        yield "from AI"
    
    mock_stream_llm.side_effect = mock_stream
    
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
    assert events[3]["components"][0]["type"] == "suggestion_chips"

@patch("app.services.chat.run_rag")
@patch("app.services.chat.stream_llm")
def test_chat_with_components(mock_stream_llm, mock_rag, client: TestClient):
    # Setup active provider
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    with patch("app.api.llm_providers.test_provider_connection") as mock_conn:
        mock_conn.return_value = (True, "OK")
        p_id = client.get("/api/v1/llm-providers").json()[0]["id"]
        client.post(f"/api/v1/llm-providers/{p_id}/activate")

    mock_rag.return_value = "Test RAG Context"
    
    async def mock_stream_code(*args, **kwargs):
        yield "print('hello')"
    
    mock_stream_llm.side_effect = mock_stream_code
    
    # Test "code" trigger
    response = client.post("/api/v1/chat", json={"message": "show me some code"})
    events = []
    for line in response.iter_lines():
        if isinstance(line, bytes): line = line.decode("utf-8")
        if line.startswith("data: ") and not line.endswith("[DONE]"):
            data = line[6:].strip()
            if data: events.append(json.loads(data))
    assert events[-1]["components"][0]["type"] == "code_block"
    
    # Test "doc" trigger
    async def mock_stream_doc(*args, **kwargs):
        yield "Searching..."
    mock_stream_llm.side_effect = mock_stream_doc
    response = client.post("/api/v1/chat", json={"message": "search in docs"})
    events = []
    for line in response.iter_lines():
        if isinstance(line, bytes): line = line.decode("utf-8")
        if line.startswith("data: ") and not line.endswith("[DONE]"):
            data = line[6:].strip()
            if data: events.append(json.loads(data))
    assert events[-1]["components"][0]["type"] == "citation_group"
    
    # Test "action" trigger
    async def mock_stream_action(*args, **kwargs):
        yield "Action..."
    mock_stream_llm.side_effect = mock_stream_action
    response = client.post("/api/v1/chat", json={"message": "do some action"})
    events = []
    for line in response.iter_lines():
        if isinstance(line, bytes): line = line.decode("utf-8")
        if line.startswith("data: ") and not line.endswith("[DONE]"):
            data = line[6:].strip()
            if data: events.append(json.loads(data))
    assert events[-1]["components"][0]["type"] == "action_buttons"
