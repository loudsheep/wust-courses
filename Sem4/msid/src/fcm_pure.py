#!/usr/bin/env python3

import numpy as np
import skfuzzy as fuzz
import matplotlib.pyplot as plt

from collections import Counter
from sklearn.metrics import accuracy_score, f1_score

import utils


FEATURES = [
    "SocialMedia_WhileEating",
    "Your overeating level",
    "Employment_Status",
    "Low_Energy",
]


def train_fuzzy_cmeans(X, n_clusters=10, m=2.0, verbose=True):
    """
    Trenuje rozmyty c-means.
    verbose=False wycisza drukowanie FPC (przydatne przy przeszukiwaniu parametrów).
    """
    data = X[FEATURES].values.T

    cntr, u, u0, d, jm, p, fpc = fuzz.cluster.cmeans(
        data, c=n_clusters, m=m, error=0.005, maxiter=1000, init=None
    )

    if verbose:
        print(f"[INFO] FPC: {fpc:.4f}")

    return cntr, u


def generate_pure_rules(X, y, cntr, u, purity_threshold=0.75):

    cluster_labels = np.argmax(u, axis=0)

    rules = []

    for k in range(cntr.shape[0]):
        idx = np.where(cluster_labels == k)[0]

        if len(idx) < 5:
            continue

        y_cluster = y.iloc[idx]
        counts = Counter(y_cluster)

        total = sum(counts.values())
        most_class, most_count = counts.most_common(1)[0]

        purity = most_count / total

        if purity < purity_threshold:
            continue

        probs = np.array(list(counts.values())) / total
        entropy = -np.sum(probs * np.log(probs + 1e-9))

        rules.append(
            {
                "cluster": k,
                "center": cntr[k],
                "output": most_class,
                "purity": purity,
                "entropy": entropy,
                "support": len(idx),
            }
        )

    rules.sort(key=lambda r: (r["purity"], r["support"]), reverse=True)

    return rules


def predict(X, rules):

    if len(rules) == 0:
        raise ValueError(
            "No rules generated. Lower purity_threshold or reduce n_clusters."
        )

    X_vals = X[FEATURES].values
    preds = []

    for x in X_vals:
        best_score = -1
        best_rule = None

        for r in rules:
            dist = np.linalg.norm(x - r["center"])
            dist = max(dist, 1e-6)

            score = r["purity"] * np.exp(-dist)

            if score > best_score:
                best_score = score
                best_rule = r

        if best_rule is None:
            best_rule = min(rules, key=lambda r: np.linalg.norm(x - r["center"]))

        preds.append(best_rule["output"])

    return np.array(preds)


def evaluate(y_true, y_pred):
    """Oblicza i wypisuje metryki. Zwraca (accuracy, f1)."""
    acc = accuracy_score(y_true, y_pred)
    f1 = f1_score(y_true, y_pred, average="weighted")

    print("\n=== RESULTS ===")
    print(f"Accuracy: {acc:.4f}")
    print(f"F1-score: {f1:.4f}")

    return acc, f1


def plot_performance_vs_m(
    X_train, y_train, X_test, y_test, m_values, n_clusters=50, purity_threshold=0.7
):
    """
    Dla każdej wartości parametru m trenuje model, generuje reguły,
    dokonuje predykcji na zbiorze testowym i zapamiętuje Accuracy oraz F1.
    Następnie rysuje wykres zależności obu metryk od m.
    """
    accuracies = []
    f1_scores = []

    for m in m_values:
        print(f"\n--- Testing m = {m:.2f} ---")
        cntr, u = train_fuzzy_cmeans(X_train, n_clusters=n_clusters, m=m, verbose=False)
        rules = generate_pure_rules(
            X_train, y_train, cntr, u, purity_threshold=purity_threshold
        )
        if len(rules) == 0:
            print(f"  [WARNING] Brak reguł dla m={m:.2f} – pomijanie.")
            accuracies.append(None)
            f1_scores.append(None)
            continue

        y_pred = predict(X_test, rules)
        acc = accuracy_score(y_test, y_pred)
        f1 = f1_score(y_test, y_pred, average="weighted")
        accuracies.append(acc)
        f1_scores.append(f1)
        print(f"  Accuracy: {acc:.4f}, F1-score: {f1:.4f}")

    # Przygotowanie wykresu – pomijamy None
    valid_indices = [i for i, v in enumerate(accuracies) if v is not None]
    m_valid = [m_values[i] for i in valid_indices]
    acc_valid = [accuracies[i] for i in valid_indices]
    f1_valid = [f1_scores[i] for i in valid_indices]

    plt.figure(figsize=(8, 5))
    plt.plot(m_valid, acc_valid, marker="o", linestyle="-", label="Accuracy")
    plt.plot(m_valid, f1_valid, marker="s", linestyle="--", label="F1-score (weighted)")
    plt.xlabel("Parametr rozmytości m")
    plt.ylabel("Wartość metryki")
    plt.title(
        f"Skuteczność vs m parameter, n_clusters={n_clusters}, purity={purity_threshold}"
    )
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()


def plot_performance_vs_n_clusters(
    X_train, y_train, X_test, y_test, n_clusters_list, m=2.0, purity_threshold=0.7
):
    """
    Dla każdej liczby klastrów z listy:
    - trenuje model,
    - generuje reguły,
    - dokonuje predykcji na zbiorze testowym,
    - zapisuje Accuracy i F1.
    Następnie rysuje wykres obu metryk w funkcji liczby klastrów.
    """
    accuracies = []
    f1_scores = []

    for n in n_clusters_list:
        print(f"\n--- Testing n_clusters = {n} ---")
        cntr, u = train_fuzzy_cmeans(X_train, n_clusters=n, m=m, verbose=False)
        rules = generate_pure_rules(
            X_train, y_train, cntr, u, purity_threshold=purity_threshold
        )

        if len(rules) == 0:
            print(f"  [WARNING] Brak reguł dla n_clusters={n} – pomijanie.")
            accuracies.append(None)
            f1_scores.append(None)
            continue

        y_pred = predict(X_test, rules)
        acc = accuracy_score(y_test, y_pred)
        f1 = f1_score(y_test, y_pred, average="weighted")
        accuracies.append(acc)
        f1_scores.append(f1)
        print(f"  Accuracy: {acc:.4f}, F1-score: {f1:.4f}")

    # Przygotowanie danych do wykresu (pomijamy None)
    valid_idx = [i for i, v in enumerate(accuracies) if v is not None]
    n_valid = [n_clusters_list[i] for i in valid_idx]
    acc_valid = [accuracies[i] for i in valid_idx]
    f1_valid = [f1_scores[i] for i in valid_idx]

    plt.figure(figsize=(8, 5))
    plt.plot(n_valid, acc_valid, marker="o", linestyle="-", label="Accuracy")
    plt.plot(n_valid, f1_valid, marker="s", linestyle="--", label="F1-score (weighted)")
    plt.xlabel("Liczba klastrów (n_clusters)")
    plt.ylabel("Wartość metryki")
    plt.title(f"Skuteczność vs liczba klastrów, m={m}, purity={purity_threshold}")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()


def plot_performance_vs_purity_threshold(
    X_train, y_train, X_test, y_test, purity_thresholds, n_clusters=60, m=2.0
):
    """
    Bada wpływ progu czystości (purity_threshold) na skuteczność.
    Trenuje model tylko raz (przy ustalonych n_clusters i m),
    a następnie dla każdej wartości progu generuje reguły i ocenia.
    """
    print("\n[INFO] Trenowanie modelu bazowego (wspólnego dla wszystkich progów)...")
    cntr, u = train_fuzzy_cmeans(X_train, n_clusters=n_clusters, m=m, verbose=False)

    accuracies = []
    f1_scores = []
    num_rules_list = []

    for thresh in purity_thresholds:
        print(f"\n--- Testing purity_threshold = {thresh:.2f} ---")
        rules = generate_pure_rules(X_train, y_train, cntr, u, purity_threshold=thresh)

        num_rules = len(rules)
        num_rules_list.append(num_rules)
        print(f"  Liczba reguł: {num_rules}")

        if num_rules == 0:
            print(f"  [WARNING] Brak reguł dla progu {thresh:.2f} – pomijanie.")
            accuracies.append(None)
            f1_scores.append(None)
            continue

        y_pred = predict(X_test, rules)
        acc = accuracy_score(y_test, y_pred)
        f1 = f1_score(y_test, y_pred, average="weighted")
        accuracies.append(acc)
        f1_scores.append(f1)
        print(f"  Accuracy: {acc:.4f}, F1-score: {f1:.4f}")

    # Wykres metryk
    valid_idx = [i for i, v in enumerate(accuracies) if v is not None]
    thresh_valid = [purity_thresholds[i] for i in valid_idx]
    acc_valid = [accuracies[i] for i in valid_idx]
    f1_valid = [f1_scores[i] for i in valid_idx]

    plt.figure(figsize=(10, 5))

    plt.subplot(1, 2, 1)
    plt.plot(thresh_valid, acc_valid, marker="o", linestyle="-", label="Accuracy")
    plt.plot(thresh_valid, f1_valid, marker="s", linestyle="--", label="F1-score")
    plt.xlabel("Próg czystości (purity_threshold)")
    plt.ylabel("Wartość metryki")
    plt.title(f"Skuteczność vs próg czystości, m={m}, n_clusters={n_clusters}")
    plt.legend()
    plt.grid(True)

    # Dodatkowy wykres: liczba reguł
    plt.subplot(1, 2, 2)
    plt.plot(
        purity_thresholds, num_rules_list, marker="D", linestyle="-", color="green"
    )
    plt.xlabel("Próg czystości")
    plt.ylabel("Liczba wygenerowanych reguł")
    plt.title("Liczba reguł vs próg czystości")
    plt.grid(True)

    plt.tight_layout()
    plt.show()


def main():

    DATA_PATH = "data/raw/Mental Health Classification.csv"
    TARGET_COL = "Depression_Type"

    m_param = 2.9
    n_clusters_param = 60
    purity_threshold_param = 0.5

    X, X_test, y, y_test = utils.load_and_split_stripped_data(DATA_PATH, TARGET_COL)
    print("\n\n" + "=" * 55)
    print("Generowanie wykresu zależności skuteczności od liczby klastrów...")
    print("=" * 55)
    n_clusters_range = list(range(10, 101, 5))
    plot_performance_vs_n_clusters(
        X,
        y,
        X_test,
        y_test,
        n_clusters_list=n_clusters_range,
        m=m_param,
        purity_threshold=purity_threshold_param,
    )

    print("\n\n" + "=" * 55)
    print("Generowanie wykresu zależności skuteczności od m...")
    print("=" * 55)
    m_values = np.arange(1.1, 3.1, 0.2)
    plot_performance_vs_m(
        X,
        y,
        X_test,
        y_test,
        m_values=m_values,
        n_clusters=n_clusters_param,
        purity_threshold=purity_threshold_param,
    )

    print("\n\n" + "=" * 55)
    print("Generowanie wykresu zależności skuteczności od progu czystości...")
    print("=" * 55)
    purity_range = np.arange(0.0, 1.01, 0.05)
    plot_performance_vs_purity_threshold(
        X,
        y,
        X_test,
        y_test,
        purity_thresholds=purity_range,
        n_clusters=n_clusters_param,
        m=m_param,
    )


if __name__ == "__main__":
    main()
