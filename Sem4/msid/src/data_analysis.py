import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

# =========================
# SETUP
# =========================
os.makedirs("plots", exist_ok=True)


def save_plot(name):
    plt.savefig(f"plots/{name}.png", dpi=300, bbox_inches="tight")
    plt.close()


# =========================
# DATA
# =========================
df = pd.read_csv("data/raw/Mental Health Classification.csv")

column_names = {
    "Gender": "Płeć",
    "Age": "Wiek",
    "Education_Level": "Poziom wykształcenia",
    "Employment_Status": "Status zatrudnienia",
    "Depression_Type": "Typ depresji",
    "Low_Energy": "Niski poziom energii",
    "Low_SelfEsteem": "Niska samoocena",
    "Search_Depression_Online": "Szukanie depresji online",
    "Worsening_Depression": "Pogłębianie depresji",
    "Your overeating level": "Poziom objadania się",
    "How many times you eat": "Ilość posiłków",
    "SocialMedia_Hours": "Godziny w mediach społecznościowych",
    "SocialMedia_WhileEating": "Media społecznościowe podczas jedzenia",
    "Sleep_Hours": "Godziny snu",
    "Self_Harm": "Samookaleczenia",
    "Mental_Health_Support": "Wsparcie zdrowia psychicznego",
    "Suicide_Attempts": "Próby samobójcze",
}

df = df.rename(columns=column_names)

fig, ax = plt.subplots(figsize=(12, 6))

counts = df["Typ depresji"].value_counts().sort_index()
bars = ax.bar(counts.index.astype(str), counts.values)

ax.set_xlabel("Typ depresji")
ax.set_ylabel("Liczba przypadków")

save_plot("depression_distribution")

fig, ax = plt.subplots(figsize=(10, 6))
pd.crosstab(df["Płeć"], df["Typ depresji"]).plot(kind="bar", stacked=True, ax=ax)

ax.set_xlabel("Płeć")
ax.set_ylabel("Liczba przypadków")
ax.legend(title="Typ depresji", bbox_to_anchor=(1.05, 1))

save_plot("gender_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))
pd.crosstab(df["Poziom wykształcenia"], df["Typ depresji"]).plot(
    kind="bar", stacked=True, ax=ax
)

ax.set_xlabel("Wykształcenie")
ax.set_ylabel("Liczba przypadków")

save_plot("education_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))
pd.crosstab(df["Status zatrudnienia"], df["Typ depresji"]).plot(
    kind="bar", stacked=True, ax=ax
)

ax.set_xlabel("Status zatrudnienia")

save_plot("employment_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))

sleep = df.groupby("Typ depresji")["Godziny snu"].mean()
bars = ax.bar(sleep.index.astype(str), sleep.values, color="skyblue")

ax.axhline(7, linestyle="--", color="red", label="7h")
ax.legend()

ax.set_ylabel("Godziny snu")

ax.bar_label(bars, fmt="%.1f", padding=3)

save_plot("sleep_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))

pd.crosstab(df["Poziom objadania się"], df["Typ depresji"]).plot(
    kind="bar", stacked=True, ax=ax
)

ax.set_xlabel("Poziom objadania się")
ax.set_ylabel("Liczba przypadków")

save_plot("overeating_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))

pd.crosstab(df["Niski poziom energii"], df["Typ depresji"]).plot(
    kind="bar", stacked=True, ax=ax
)

ax.set_xlabel("Niski poziom energii")
ax.set_ylabel("Liczba przypadków")

save_plot("low_energy_vs_depression")

fig, ax = plt.subplots(figsize=(10, 6))

pd.crosstab(df["Media społecznościowe podczas jedzenia"], df["Typ depresji"]).plot(
    kind="bar", stacked=True, ax=ax
)

ax.set_xlabel("Social media podczas jedzenia")
ax.set_ylabel("Liczba przypadków")

save_plot("social_media_eating_vs_depression")

# =========================
# DONE
# =========================
print("Saved all plots to /plots/")
