from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.file_upload import router as file_router
from app.api.stop_details import router as stops_router
from app.api.network import router as network_router

app = FastAPI(title="GTFS Timetable API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(file_router)
app.include_router(stops_router)
app.include_router(network_router)

if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
