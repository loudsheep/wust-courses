import os
from cryptography.fernet import Fernet
from fastapi import HTTPException


def get_fernet() -> Fernet:
    key = os.getenv("SECRET_KEY")
    if not key:
        raise HTTPException(status_code=500, detail="SECRET_KEY is not configured")
    return Fernet(key.encode())
