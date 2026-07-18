from typing import Dict, Optional

from pydantic import BaseModel, Field


class FoodFeatureRequest(BaseModel):
    foodId: int = Field(..., gt=0)
    features: Dict[str, float]


class PredictionResponse(BaseModel):
    score: float
    level: str
    confidence: float
    modelVersion: str
    modelName: str
    algorithm: str
    accuracy: Optional[float] = None
