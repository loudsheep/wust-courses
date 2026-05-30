import time
from typing import Tuple, Dict, Any
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, f1_score, accuracy_score
from sklearn.base import BaseEstimator


def load_and_split_data(
    filepath: str, target_col: str, test_size: float = 0.2, random_state: int = 42
) -> Tuple[pd.DataFrame, pd.DataFrame, pd.Series, pd.Series]:
    """
    Load data from a CSV file and split it into training and testing sets.

    Parameters:
    - filepath: str - Path to the CSV file.
    - target_col: str - Name of the target column.
    - test_size: float - Proportion of the dataset to include in the test split.
    - random_state: int - Controls the randomness of the split.

    Returns:
    - X_train: pd.DataFrame - Training features.
    - X_test: pd.DataFrame - Testing features.
    - y_train: pd.Series - Training target.
    - y_test: pd.Series - Testing target.
    """

    df = pd.read_csv(filepath)

    leaky_columns = [
        target_col,
        "Depression_Score",
        "Symptoms",
        "Nervous_Level",
        "Coping_Methods",
    ]

    # Tworzymy zbiór cech bazujących w 100% na stylu życia i demografii
    X = df.drop(columns=leaky_columns)
    y = df[target_col]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=test_size, random_state=random_state, stratify=y
    )

    return X_train, X_test, y_train, y_test  # type: ignore


def load_and_split_stripped_data(
    filepath: str, target_col: str, test_size: float = 0.2, random_state: int = 42
) -> Tuple[pd.DataFrame, pd.DataFrame, pd.Series, pd.Series]:
    """
    Load data from a CSV file and split it into training and testing sets.
    """

    df = pd.read_csv(filepath)

    feature_cols = [
        "Employment_Status",
        "Low_Energy",
        "Your overeating level",
        "SocialMedia_WhileEating",
    ]

    missing = [c for c in feature_cols + [target_col] if c not in df.columns]
    if missing:
        raise ValueError(f"Missing columns in dataset: {missing}")

    X = df[feature_cols]
    y = df[target_col]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=test_size, random_state=random_state, stratify=y
    )

    return X_train, X_test, y_train, y_test  # type: ignore


def time_function(func):
    """
    Decorator to measure the execution time of a function.

    Parameters:
    - func: Callable - The function to be timed.

    Returns:
    - wrapper: Callable - The wrapped function with timing.
    """

    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        print(
            f"Execution time for {func.__name__}: {end_time - start_time:.4f} seconds"
        )
        return result

    return wrapper


def evaluate_model(
    model: BaseEstimator, X_test: pd.DataFrame, y_test: pd.Series
) -> Dict[str, Any]:
    """
    Evaluate a trained model on the test set and return performance metrics.

    Parameters:
    - model: BaseEstimator - The trained model to evaluate.
    - X_test: pd.DataFrame - Testing features.
    - y_test: pd.Series - Testing target.

    Returns:
    - metrics: Dict[str, Any] - A dictionary containing accuracy, F1 score, and classification report.
    """

    y_pred = model.predict(X_test)  # type: ignore

    accuracy = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred, average="macro")
    report = classification_report(y_test, y_pred)

    return {"accuracy": accuracy, "f1_score": f1, "classification_report": report}


@time_function
def train_model(
    model: BaseEstimator, X_train: pd.DataFrame, y_train: pd.Series
) -> BaseEstimator:
    """
    Train a machine learning model on the training data.

    Parameters:
    - model: BaseEstimator - The machine learning model to train.
    - X_train: pd.DataFrame - Training features.
    - y_train: pd.Series - Training target.

    Returns:
    - model: BaseEstimator - The trained model.
    """
    model.fit(X_train, y_train)  # type: ignore
    return model


def save_model(model: BaseEstimator, filepath: str) -> None:
    """
    Save a trained model to a file using joblib.

    Parameters:
    - model: BaseEstimator - The trained model to save.
    - filepath: str - Path to the file where the model will be saved.
    """
    import joblib

    joblib.dump(model, filepath)


def load_model(filepath: str) -> BaseEstimator:
    """
    Load a trained model from a file using joblib.

    Parameters:
    - filepath: str - Path to the file where the model is saved.

    Returns:
    - model: BaseEstimator - The loaded model.
    """
    import joblib

    return joblib.load(filepath)
