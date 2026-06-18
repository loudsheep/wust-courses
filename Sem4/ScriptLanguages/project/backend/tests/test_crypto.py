import os
import pytest
from unittest.mock import patch
from app.services.crypto import get_fernet
from cryptography.fernet import Fernet
from fastapi import HTTPException

def test_get_fernet_success():
    key = Fernet.generate_key().decode()
    with patch.dict(os.environ, {"SECRET_KEY": key}):
        fernet = get_fernet()
        assert isinstance(fernet, Fernet)

def test_get_fernet_no_key():
    with patch.dict(os.environ, {}, clear=True):
        if "SECRET_KEY" in os.environ:
            del os.environ["SECRET_KEY"]
        with pytest.raises(HTTPException) as excinfo:
            get_fernet()
        assert excinfo.value.status_code == 500
        assert "SECRET_KEY is not configured" in excinfo.value.detail
