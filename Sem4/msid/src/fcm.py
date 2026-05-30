#!/usr/bin/env python3

import numpy as np
import skfuzzy as fuzz

from collections import Counter
from sklearn.metrics import accuracy_score, f1_score

import utils


def train_fuzzy_cmeans(X, n_clusters=10):
    """
    Uczy fuzzy c-means na 4 zmiennych wejściowych
    """

    features = [
        "SocialMedia_WhileEating",
        "Your overeating level",
        "Employment_Status",
        "Low_Energy",
    ]

    data = X[features].values.T  # (features, samples)

    cntr, u, u0, d, jm, p, fpc = fuzz.cluster.cmeans(
        data, c=n_clusters, m=2.0, error=0.005, maxiter=1000, init=None
    )

    print(f"[INFO] Fuzzy Partition Coefficient (FPC): {fpc:.4f}")

    return cntr, u


def generate_rules(X, y, cntr, u):
    """
    Tworzy reguły:
    IF cluster_k THEN output = najczęstsza klasa
    """

    cluster_labels = np.argmax(u, axis=0)

    rules = []

    for k in range(cntr.shape[0]):
        idx = np.where(cluster_labels == k)[0]

        if len(idx) == 0:
            continue

        y_cluster = y.iloc[idx]
        most_common = Counter(y_cluster).most_common(1)[0][0]

        rules.append(
            {
                "cluster": k,
                "center": cntr[k],
                "output": most_common,
                "support": len(idx),
            }
        )

    rules.sort(key=lambda r: r["support"], reverse=True)

    return rules


def predict(X, rules):
    """
    Najbliższy centroid (prosty fuzzy classifier)
    """

    features = [
        "SocialMedia_WhileEating",
        "Your overeating level",
        "Employment_Status",
        "Low_Energy",
    ]

    X_vals = X[features].values

    preds = []

    for x in X_vals:
        best_rule = None
        best_dist = float("inf")

        for rule in rules:
            dist = np.linalg.norm(x - rule["center"])

            if dist < best_dist:
                best_dist = dist
                best_rule = rule

        preds.append(best_rule["output"])

    return np.array(preds)


def evaluate(y_true, y_pred):
    acc = accuracy_score(y_true, y_pred)
    f1 = f1_score(y_true, y_pred, average="weighted")

    print("\n=== RESULTS ===")
    print(f"Accuracy: {acc:.4f}")
    print(f"F1-score : {f1:.4f}")


def main():
    DATA_PATH = "data/raw/Mental Health Classification.csv"
    TARGET_COL = "Depression_Type"

    # load data
    X, X_test, y, y_test = utils.load_and_split_stripped_data(DATA_PATH, TARGET_COL)

    print("[INFO] Training fuzzy c-means...")

    cntr, u = train_fuzzy_cmeans(X, n_clusters=10)

    print("[INFO] Generating rules...")
    rules = generate_rules(X, y, cntr, u)

    print(rules)

    print("\n=== TOP RULES ===")
    for r in rules[:10]:
        print(f"Cluster {r['cluster']} -> class {r['output']} (n={r['support']})")

    print("\n[INFO] Predicting...")

    y_pred = predict(X_test, rules)

    evaluate(y_test, y_pred)


if __name__ == "__main__":
    main()
