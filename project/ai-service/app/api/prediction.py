from fastapi import APIRouter, HTTPException

from app.model.random_forest_model import ModelUnavailableError
from app.schema.prediction import FoodFeatureRequest, PredictionResponse
from app.service.prediction_service import PredictionService

router = APIRouter()
service = PredictionService()


@router.post("/predict", response_model=PredictionResponse)
def predict(request: FoodFeatureRequest) -> PredictionResponse:
    try:
        return service.predict(request)
    except ModelUnavailableError as error:
        raise HTTPException(status_code=503, detail=str(error))
    except ValueError as error:
        raise HTTPException(status_code=422, detail=str(error))
