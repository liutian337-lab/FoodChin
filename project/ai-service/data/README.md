# Bootstrap training data

`bootstrap_training.csv` exists only to generate a deployable development model for HTTP integration tests. It is not a real food-quality dataset and must not be used to report EI-paper accuracy, precision, recall, F1, ROC, or any research conclusion.

Replace it with a governed, labeled experimental dataset before conducting experiments, then run `train_random_forest.py --data <dataset.csv> --output models/random_forest.joblib --version <version>`.
