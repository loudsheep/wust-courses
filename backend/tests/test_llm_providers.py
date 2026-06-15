from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient
from app.models.llm_provider import LLMProvider

def test_create_provider(client: TestClient):
    payload = {
        "name": "Test Gemini",
        "provider": "gemini",
        "model": "gemini-1.5-pro",
        "api_key": "test-api-key",
        "base_url": None
    }
    response = client.post("/api/v1/llm-providers", json=payload)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "Test Gemini"
    assert data["provider"] == "gemini"
    assert "id" in data

def test_list_providers(client: TestClient):
    client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    client.post("/api/v1/llm-providers", json={
        "name": "P2", "provider": "anthropic", "model": "claude-3", "api_key": "k2"
    })
    
    response = client.get("/api/v1/llm-providers")
    assert response.status_code == 200
    assert len(response.json()) == 2

def test_get_provider(client: TestClient):
    create_resp = client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    provider_id = create_resp.json()["id"]
    
    response = client.get(f"/api/v1/llm-providers/{provider_id}")
    assert response.status_code == 200
    assert response.json()["name"] == "P1"

def test_delete_provider(client: TestClient):
    create_resp = client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    provider_id = create_resp.json()["id"]
    
    response = client.delete(f"/api/v1/llm-providers/{provider_id}")
    assert response.status_code == 204
    
    get_resp = client.get(f"/api/v1/llm-providers/{provider_id}")
    assert get_resp.status_code == 404

@patch("app.api.llm_providers.test_provider_connection")
def test_toggle_provider_active_success(mock_test_conn, client: TestClient):
    mock_test_conn.return_value = (True, "OK")
    
    create_resp = client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "ollama", "model": "llama3", "api_key": None, "base_url": "http://localhost:11434"
    })
    provider_id = create_resp.json()["id"]
    
    response = client.post(f"/api/v1/llm-providers/{provider_id}/toggle-active")
    assert response.status_code == 200
    assert response.json()["success"] is True
    assert "toggled" in response.json()["message"]
    
    # Verify it is active (it was active by default on create, so toggle makes it INACTIVE)
    # Wait, the commit says: is_active=True # Default to active on creation
    # So if it was True, toggle makes it False.
    get_resp = client.get(f"/api/v1/llm-providers/{provider_id}")
    assert get_resp.json()["is_active"] is False

@patch("app.api.llm_providers.test_provider_connection")
def test_toggle_provider_active_failure(mock_test_conn, client: TestClient):
    # To test failure, it must be inactive first, because validation/test connection 
    # only happens when activating (if not cfg.is_active: ...)
    
    # Create and it defaults to active
    create_resp = client.post("/api/v1/llm-providers", json={
        "name": "P1", "provider": "openai", "model": "gpt-4", "api_key": "k1"
    })
    provider_id = create_resp.json()["id"]
    
    # Toggle to inactive (no validation here)
    client.post(f"/api/v1/llm-providers/{provider_id}/toggle-active")
    
    # Now it's inactive. Try to toggle back to active (this will trigger validation)
    mock_test_conn.return_value = (False, "Invalid API key")
    response = client.post(f"/api/v1/llm-providers/{provider_id}/toggle-active")
    
    assert response.status_code == 200
    assert response.json()["success"] is False
    assert response.json()["message"] == "Invalid API key"
    
    # Verify it is STILL inactive
    get_resp = client.get(f"/api/v1/llm-providers/{provider_id}")
    assert get_resp.json()["is_active"] is False
