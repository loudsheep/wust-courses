import zipfile
import io
import traceback
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.load_data import process_zip

router = APIRouter()

@router.get("/")
def read_root() -> dict[str, str]:
    return {"status": "ok"}

@router.post("/upload")
async def upload_gtfs(file: UploadFile = File(...), db: Session = Depends(get_db)):
    if not file.filename.endswith(".zip"):
        raise HTTPException(status_code=400, detail="Only ZIP files are allowed")

    try:
        contents = await file.read()
        z = zipfile.ZipFile(io.BytesIO(contents))
        process_zip(z, db)
        return {"message": "Data loaded successfully"}
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Error processing file: {str(e)}")
