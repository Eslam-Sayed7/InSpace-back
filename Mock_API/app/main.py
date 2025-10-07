from fastapi import FastAPI
from .routes import router

app = FastAPI(title="UI Simulation Server")
app.include_router(router)
@app.get("/")
def home():
    return {"message": "UI Simulation Server is running"}
