import json
from unittest.mock import patch
from fastapi.testclient import TestClient

def test_conversations_lifecycle(client: TestClient):
    # 1. Setup provider
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    # it is active by default now
    
    # 2. Start a chat to create a conversation
    async def mock_agent(*args, **kwargs):
        yield {"type": "content", "text": "Hello"}
        yield {"type": "retrieved_chunks", "chunks": []}

    with patch("app.services.chat.run_agent", side_effect=mock_agent):
        response = client.post("/api/v1/chat", json={"message": "hi"})
        assert response.status_code == 200
        
        # Extract conversation_id from stream
        conv_id = None
        for line in response.iter_lines():
            if isinstance(line, bytes): line = line.decode("utf-8")
            if line.startswith("data: ") and not line.endswith("[DONE]"):
                data = json.loads(line[6:].strip())
                if "conversation_id" in data:
                    conv_id = data["conversation_id"]
                    break
        
        assert conv_id is not None

    # 3. List conversations
    resp = client.get("/api/v1/chat/conversations")
    assert resp.status_code == 200
    conversations = resp.json()
    assert len(conversations) >= 1
    assert any(c["id"] == conv_id for c in conversations)
    
    # Check summary fields
    conv_summary = next(c for c in conversations if c["id"] == conv_id)
    assert conv_summary["message_count"] == 2 # "hi" and "Hello"
    assert "title" in conv_summary

    # 4. Get specific conversation
    resp = client.get(f"/api/v1/chat/conversations/{conv_id}")
    assert resp.status_code == 200
    assert resp.json()["id"] == conv_id

    # 5. List messages
    resp = client.get(f"/api/v1/chat/conversations/{conv_id}/messages")
    assert resp.status_code == 200
    messages = resp.json()
    assert len(messages) == 2
    assert messages[0]["role"] == "user"
    assert messages[0]["content"] == "hi"
    assert messages[1]["role"] == "assistant"
    assert messages[1]["content"] == "Hello"

    # 6. Delete conversation
    resp = client.delete(f"/api/v1/chat/conversations/{conv_id}")
    assert resp.status_code == 204
    
    # Verify deleted
    resp = client.get(f"/api/v1/chat/conversations/{conv_id}")
    assert resp.status_code == 404
    
    resp = client.get("/api/v1/chat/conversations")
    assert not any(c["id"] == conv_id for c in resp.json())

def test_list_providers_active_only(client: TestClient):
    # P1: active (default)
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    
    # P2: create then deactivate
    resp = client.post("/api/v1/llm-providers", json={
        "name": "P2", "provider": "anthropic", "model": "claude-3", "api_key": "k2"
    })
    p2_id = resp.json()["id"]
    client.post(f"/api/v1/llm-providers/{p2_id}/toggle-active") # toggles to False
    
    # List all
    resp = client.get("/api/v1/llm-providers")
    assert len(resp.json()) >= 2
    
    # List active only
    resp = client.get("/api/v1/llm-providers?active_only=true")
    active_providers = resp.json()
    assert all(p["is_active"] for p in active_providers)
    assert not any(p["id"] == p2_id for p in active_providers)

def test_get_provider_logic(client: TestClient):
    # 1. Create P1 (active)
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    
    # 2. Create P2 (active, later created_at)
    resp = client.post("/api/v1/llm-providers", json={
        "name": "P2", "provider": "anthropic", "model": "claude-3", "api_key": "k2"
    })
    p2_id = resp.json()["id"]
    
    # 3. Chat without provider_id should use P2 (latest active)
    async def mock_agent(*args, **kwargs):
        # We can check which provider was used if we inspect args, 
        # but let's just check if it succeeds for now as a proxy for 'found an active provider'
        yield {"type": "content", "text": "Hi"}
        yield {"type": "retrieved_chunks", "chunks": []}

    with patch("app.services.chat.run_agent", side_effect=mock_agent) as mock_run:
        client.post("/api/v1/chat", json={"message": "hi"})
        # The first arg to run_agent is the provider config
        # We want to make sure it's P2
        args, kwargs = mock_run.call_args
        provider_cfg = args[0]
        assert provider_cfg.id == p2_id
