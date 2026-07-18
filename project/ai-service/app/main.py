from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.prediction import router as prediction_router
from app.model.model_registry import get_prediction_model
from app.model.random_forest_model import ModelUnavailableError


@asynccontextmanager
async def lifespan(_: FastAPI):
    try:
        get_prediction_model()
    except ModelUnavailableError as error:
        raise RuntimeError("AI model startup check failed: " + str(error)) from error
    yield


app = FastAPI(title="FoodChin AI Evaluation Service", version="0.1.0", lifespan=lifespan)
app.include_router(prediction_router)
