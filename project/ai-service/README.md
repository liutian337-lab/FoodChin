# FoodChin AI Evaluation Service

This service owns food-quality prediction. The Spring Boot backend calls `POST /predict`; it does not embed Python or model logic.

## Run

```bash
python -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

On Windows PowerShell, activate the virtual environment with `.\.venv\Scripts\Activate.ps1`.

## Contract

```json
POST /predict
{
  "foodId": 7,
  "features": {"transportTemperature": 4.2}
}
```

## Train the Random Forest model

The service does not generate a model from fabricated data. Train it from a labeled CSV whose numeric feature columns are generic names such as `temperature`, `humidity`, `storage_time`, and whose label column is named `score`.

```bash
python train_model.py
python train_random_forest.py --data /path/to/labeled_food_quality.csv --output models/random_forest.joblib --version rf-v1
AI_MODEL_PATH=models/random_forest.joblib uvicorn app.main:app --host 0.0.0.0 --port 8000
```

`train_model.py` creates a development-only bootstrap artifact; it is not valid for research experiments. The artifact stores feature order, model name, version, algorithm, and hold-out quality-grade accuracy. FastAPI now fails startup with a clear message if no trained artifact is available, and returns HTTP 422 when required features are absent.
