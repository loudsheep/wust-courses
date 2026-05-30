# Porównanie systemów logiki rozmytej i uczenia maszynowego w klasyfikacji zdrowia psychicznego

**Autorzy:** Kinga Adamska 284236, Roch Mykietów 284240

---

## Charakterystyka problemu

Analizowany problem to przewidywanie i klasyfikacja typu depresji na podstawie danych
zebranych w zbiorze **Mental Health Classification**.

Głównym celem analizy jest porównanie zachowania modeli uczenia maszynowego z logiką rozmytą na danych, które posiadają wartości zarówno ciągłe jak i dyskretne.

### Znaczenie wartości

Wyjaśnienie najważniejszych wartości branych pod uwagę przy analizie danych

<div style="display: flex; gap: 40px;">

<div>

| Kod | Employment_Status |
| --- | ----------------- |
| 0   | Bezrobotny        |
| 1   | Student           |
| 2   | Zatrudniony       |
| 3   | Samozatrudniony   |
| 4   | Inne              |

</div>
<div>

| Kod  | Overeating_Level |
| ---- | ---------------- |
| 0    | Brak             |
| 1–4  | Łagodny          |
| 5–8  | Umiarkowany      |
| 9–12 | Ciężki           |

</div>
</div>

<div style="display: flex; gap: 40px;">
<div>

| Kod | SocialMedia_WhileEating |
| --- | ----------------------- |
| 0   | Nigdy                   |
| 1   | Rzadko                  |
| 2   | Często                  |
| 3   | Zawsze                  |

</div>

<div>

| Kod | Low_Energy   |
| --- | ------------ |
| 0   | Nie          |
| 1   | Tak          |
| 2   | Sporadycznie |

</div>
</div>

| Kod | Depression_Type                                         |
| --- | ------------------------------------------------------- |
| 0   | Brak depresji o znaczeniu klinicznym                    |
| 1   | Depresja minimalna / lekka                              |
| 2   | Depresja umiarkowana                                    |
| 3   | Depresja umiarkowanie ciężka                            |
| 4   | Depresja ciężka                                         |
| 5   | Uporczywe zaburzenie depresyjne (Dystymia)              |
| 6   | Zaburzenie afektywne sezonowe (Depresja sezonowa)       |
| 7   | Depresja okołoporodowa / poporodowa                     |
| 8   | Epizod depresyjny w przebiegu zaburzenia dwubiegunowego |
| 9   | Depresja sytuacyjna / reaktywna                         |
| 10  | Depresja psychotyczna                                   |
| 11  | Inne określone zaburzenia depresyjne                    |

### Oceniane parametry

By wybrać technikę oceny skuteczności metody klasyfikacji zostały
przeanalizowane dane, na bazie których szkolony jest model.

![Rozkład typów depresji](../plots/depression_distribution.png)

Ze względu na małą równomierność analizowanych danych - znaczącą przewagę przypadków
depresji sytuacyjej/reaktywnej branym pod uwagę parametrem jest **F1 score** - jego
wartość pozwala na uwzględnienie częstości false-positive, które jest szczególnie
ważne przy małej równomierności danych.

Oczywistym wyborem było także **accuracy** klasyfikacji.

## Stosowane metody klasyfikacji

### Stosowane modele logiki rozmytej

W ramach opracowywania rozwiązania zadana wytestowano
trzy techniki fuzzy:

- zwykłe rule-based fuzzy logic wykorzystuące ręcznie utworzone reguły, na podstawie
  statycznej analizy danych
- **FCM** - fuzzy c-means - metoda oparta na fuzzy-clusteringu danych
- FCM z purity threshold - wykorzystanie c-means rozszerzone o
  zastosowanie ograniczenia używanych klastrów do tych spełniających
  określoną "czystość" - stopień w jakim dane zawierające się w klastrze
  mają zgodność klasyfikowanej cechy depression type

Ze względu na drastyczną różnicę na dalszych stronach raportu analizowane będą
głównie parametry stosowane w trzecim modelu logiki rozmytej.

### Stosowane modele uczenia maszynowego

Dla oceny możliwości rozpoznawania wzorców użyto i
porównano ze sobą 4 popularne modele uczenia maszynowego:

1. **Regresja logistyczna (Logistic Regression):**
   Podstawowy, liniowy model klasyfikacyjny. Działa poprzez oszacowanie
   prawdopodobieństwa przynależności do danej klasy na podstawie
   liniowej kombinacji cech wejściowych, a następnie przepuszczenie
   wyniku przez funkcję sigmoidalną.
2. **Drzewo decyzyjne (Decision Tree):**
   Algorytm dokonujący hierarchicznego, binarnego podziału
   przestrzeni cech wejściowych aż do osiągnięcia jednorodnych klas
   w tzw. liściach. Model cechuje się wysoką interpretowalnością,
   ale jest podatny na przeuczenie.
3. **Las losowy (Random Forest):**
   Metoda zespołowa (ang. _ensemble method_) łącząca wyniki wielu
   niezależnych drzew decyzyjnych (tzw. zjawisko "mądrości tłumu").
   Dzięki uśrednianiu, wariant ten znacząco redukuje ryzyko
   przeuczenia i wariancję, zachowując przy tym wysoką moc predykcyjną.
4. **Gradient Boosting oparty o histogramy (HistGradientBoosting):**
   Algorytm sekwencyjny budujący kolejne drzewa tak, by
   korygować błędy popełniane przez swoich poprzedników.
   Optymalizacja histogramowa pozwala znacząco przyspieszyć
   proces tworzenia wezłów i podziału zbiorów dla dużych
   wolumenów danych.

## Testy parametrów algorytmów uczenia maszynowego dla pełnego zbioru danych

Dla wszystkich przetestowanych modeli wykonano eksperymenty
dostrajania hiperparametrów metodą optymalizacji wrażliwości
w celu znalezienia kompromisu dla jak najwyższej precyzji w
odniesieniu do metryk **Accuracy** (Dokładność) oraz **F1 Score**.

### Random Forest: Liczba estymatorów (`n_estimators`)

Badanie wpływu liczby wykorzystanych drzew w zespole na jakość końcowej klasyfikacji.

![Random Forest - Estimators](../plots/rf_estimators_all_features.png)

| Liczba estymatorów | Accuracy | F1 Score |
| :----------------- | :------- | :------- |
| 10                 | 0.9800   | 0.9546   |
| 25                 | 0.9825   | 0.9519   |
| 50                 | 0.9825   | 0.9519   |
| 100                | 0.9850   | 0.9615   |
| 200                | 0.9850   | 0.9615   |
| 500                | 0.9850   | 0.9615   |

**Analiza:**
Skuteczność klasyfikacji ulega znacznej poprawie na początku wzrostu
wielkości lasu i osiąga swój optymalny pułap około 100 estymatorów
(Accuracy 98.5%). Zwiększanie rozmiaru lasu powyżej 100 drzew
decyzyjnych marnuje jedynie czas i zasoby obliczeniowe nie wnosząc
żadnej poprawy do wskaźników jakości klasyfikacji.

### Random Forest: Maksymalna głębokość (`max_depth`)

Analiza zachowania złożoności modelu determinowanej przez
maksymalną głębokość drzew składowych.

![Random Forest - Max Depth](../plots/rf_max_depth_all_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.7125   | 0.6453   |
| 10                   | 0.9800   | 0.9408   |
| 20                   | 0.9850   | 0.9615   |
| 50                   | 0.9850   | 0.9615   |
| 100                  | 0.9850   | 0.9615   |

**Analiza:**
Płytkie drzewa (głębokość 5) są niedouczone (ang. _underfitting_)
i osiągają relatywnie niskie rezultaty (Accuracy: 71.2%).
Dopiero zagłębienie drzew od ok. 20 poziomów zapewnia najwyższą,
stabilną jakość wyników (98.5%).

### Decision Tree: Maksymalna głębokość (`max_depth`)

Taki sam parametr jak powyżej przetestowano dla pojedynczego drzewa decyzyjnego.

![Decision Tree - Max Depth](../plots/decision_tree_depth_all_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.5525   | 0.5637   |
| 10                   | 0.8700   | 0.7920   |
| 20                   | 0.9850   | 0.9615   |
| 50                   | 0.9850   | 0.9615   |
| 100                  | 0.9850   | 0.9615   |

**Analiza:**
Pojedyncze drzewo jest jeszcze mocniej czułe na zbyt duże
ograniczenie swojej struktury (głębokość 5 to zaledwie Accuracy
55.25%). Pozwolenie drzewu na optymalny rozwój struktury decyzyjnej
do 20 poziomów zrównuje jego wynik z modelem
Random Forest (Accuracy: 98.5%).

### Logistic Regression: Odwrotność siły regularyzacji (`C`)

Wpływ hiperparametru regularyzacji (przeciwdziałania
przeuczeniu wektorów wag) na trafność modelu liniowego.

![Logistic Regression - C](../plots/logistic_regression_C_all_features.png)

| C (odwrotność siły) | Accuracy | F1 Score |
| :------------------ | :------- | :------- |
| 0.01                | 0.3950   | 0.1152   |
| 0.1                 | 0.5225   | 0.4921   |
| 1.0                 | 0.5900   | 0.7293   |
| 10.0                | 0.6250   | 0.8067   |
| 100.0               | 0.6225   | 0.8080   |

**Analiza:**
Regresja logistyczna radzi sobie bardzo słabo z postawionym
wyzwaniem ze względu na swoje liniowe ograniczenia, osiągając
maksymalnie zaledwie 62.5% dokładności klasyfikacji (dla wartości
współczynnika C = 10.0, gdzie silniejsza regularyzacja dusi proces
uczenia, a słabsza nie polepsza już dopasowania do wariancji).

### HistGradientBoosting: Współczynnik uczenia (`learning_rate`)

Wrażliwość współczynnika optymalizacji procesu gradientowego
w iteracyjnym budowaniu drzew.

![HistGradient Boosting - Learning Rate](../plots/hist_gradient_boosting_lr_all_features.png)

| Learning Rate | Accuracy | F1 Score |
| :------------ | :------- | :------- |
| 0.01          | 0.9800   | 0.9408   |
| 0.1           | 0.9750   | 0.9260   |
| 0.2           | 0.3250   | 0.2018   |
| 0.5           | 0.2625   | 0.1793   |
| 1.0           | 0.0875   | 0.0564   |
| 10.0          | 0.2650   | 0.0404   |

**Analiza:**
Kluczem do optymalnego gradient boostingu okazały się bardzo niskie wartości
współczynnika uczenia. Optymalną wydajność osiągnięto przy bardzo wolnym
uczeniu i dopasowywaniu wag `learning_rate` na poziomie zaledwie
0.01 (Accuracy 98%). Znaczne podniesienie progu nauki doprowadza szybko
do "wystrzelenia" i utraty sterowności po funkcji błędu (drastyczny
spadek dokładności już przy LR=0.2).

## Testy parametrów algorytmów uczenia maszynowego na ograniczonym zbiorze danych

Eksperymenty powtórzono na **ograniczonym zbiorze cech**, identycznym jak ten
stosowany przez system logiki rozmytej: `Employment_Status`, `Low_Energy`,
`Your overeating level`, `SocialMedia_WhileEating`. Wszystkie cztery cechy
są dyskretne (kategoryczne), co stanowi znaczne utrudnienie względem pełnego
zbioru zawierającego m.in. ciągłe wskaźniki kliniczne.

### Random Forest: Liczba estymatorów (`n_estimators`)

![Random Forest - Estimators (4 features)](../plots/rf_estimators_4_features.png)

| Liczba estymatorów | Accuracy | F1 Score |
| :----------------- | :------- | :------- |
| 10                 | 0.8250   | 0.7360   |
| 25                 | 0.8250   | 0.6814   |
| 50                 | 0.8250   | 0.6814   |
| 100                | 0.8250   | 0.6814   |
| 200                | 0.8250   | 0.6814   |
| 500                | 0.8250   | 0.6814   |

**Analiza:**
Accuracy stabilizuje się już od 10 estymatorów na poziomie 82.5% —
wyraźny pułap wynikający z ubóstwa przestrzeni cech.
Interesująca jest nieznacznie wyższa wartość F1 dla małego lasu (10 drzew: 0.7360),
która spada i stabilizuje się przy 25+ drzewach (0.6814).
Wynika to z losowości małego zespołu: przy 10 drzewach zdarzają się
trafienia rzadkich klas, których stabilny większy las już nie przewiduje.
Dodawanie kolejnych drzew nie wnosi żadnej poprawy — wszystkie
modele napotykają ten sam nieusuwany błąd informacyjny.

### Random Forest: Maksymalna głębokość (`max_depth`)

![Random Forest - Max Depth (4 features)](../plots/rf_max_depth_4_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.6225   | 0.4744   |
| 10                   | 0.8075   | 0.6749   |
| 20                   | 0.8250   | 0.6814   |
| 50                   | 0.8250   | 0.6814   |
| 100                  | 0.8250   | 0.6814   |

**Analiza:**
Wzorzec niedouczenia dla płytkich drzew pozostaje taki sam jak
w pełnym zbiorze — głębokość 5 daje zaledwie 62.25% dokładności.
Optymalny pułap (82.5%) osiągany jest już przy głębokości 20 i
nie ulega dalszej poprawie. Warto zauważyć, że maksymalny wynik
jest o ponad 16 punktów procentowych niższy niż w pełnym zbiorze
(98.5%), co potwierdza, że **ograniczenie do 4 dyskretnych cech
jest fundamentalną barierą informacyjną**, a nie kwestią
doboru hiperparametrów.

### Decision Tree: Maksymalna głębokość (`max_depth`)

![Decision Tree - Max Depth (4 features)](../plots/decision_tree_depth_4_features.png)

| Maksymalna głębokość | Accuracy | F1 Score |
| :------------------- | :------- | :------- |
| 5                    | 0.4775   | 0.3402   |
| 10                   | 0.7925   | 0.6711   |
| 20                   | 0.8250   | 0.6814   |
| 50                   | 0.8250   | 0.6814   |
| 100                  | 0.8250   | 0.6814   |

**Analiza:**
Pojedyncze drzewo jest jeszcze bardziej wrażliwe na ograniczenie głębokości
niż las losowy — głębokość 5 to zaledwie 47.75% dokładności (F1: 0.34),
co jest poniżej progu użyteczności klinicznej.
Pełna struktura drzewa (od głębokości 20) osiąga ten sam sufit co Random Forest
(82.5% accuracy), choć F1 jest marginalnie niższy dla głębokości 10 (0.6711 vs 0.6749).
Wyniki potwierdzają, że problemem jest brak cech, a nie zdolność modelu
do ich wyeksplorowania.

### Logistic Regression: Odwrotność siły regularyzacji (`C`)

![Logistic Regression - C (4 features)](../plots/logistic_regression_C_4_features.png)

| C (odwrotność siły) | Accuracy | F1 Score |
| :------------------ | :------- | :------- |
| 0.01                | 0.3650   | 0.0769   |
| 0.1                 | 0.4175   | 0.2929   |
| 1.0                 | 0.4300   | 0.3007   |
| 10.0                | 0.3975   | 0.2914   |
| 100.0               | 0.4100   | 0.2987   |

**Analiza:**
Regresja logistyczna na ograniczonym zbiorze radzi sobie katastrofalnie —
maksymalna dokładność to 43% przy C=1.0, co jest wyraźnie gorsze
nawet niż już słaby wynik 62.5% na pełnym zbiorze cech.
Przy C=0.01 F1 spada do 0.077, co oznacza, że mocna regularyzacja
prawie całkowicie uniemożliwia uczenie. Nieliniowalność problemu
klasyfikacji 12 typów depresji na bazie 4 dyskretnych zmiennych
czyni model liniowy praktycznie bezużytecznym.
Przy C≥1.0 wyniki są zbliżone, lecz nigdy nie przekraczają 43%,
co dowodzi, że mamy do czynienia z nieredukowalnym błędem modelu liniowego.

### HistGradientBoosting: Współczynnik uczenia (`learning_rate`)

![HistGradient Boosting - Learning Rate (4 features)](../plots/hist_gradient_boosting_lr_4_features.png)

| Learning Rate | Accuracy | F1 Score |
| :------------ | :------- | :------- |
| 0.01          | 0.8250   | 0.6814   |
| 0.1           | 0.8250   | 0.6814   |
| 0.2           | 0.8250   | 0.6814   |
| 0.5           | 0.2425   | 0.1352   |
| 1.0           | 0.3275   | 0.1365   |
| 10.0          | 0.3725   | 0.2146   |

**Analiza:**
Gradient Boosting na ograniczonym zbiorze wykazuje szersze okno
optymalnych współczynników uczenia niż w pełnym zbiorze —
wartości 0.01, 0.1 oraz 0.2 dają identyczne, najlepsze wyniki
(Accuracy 82.5%, F1 0.6814). Wynika to z prostoty przestrzeni
hiperpowierzchni: z zaledwie 4 dyskretnymi cechami drzewo
eksploatuje cały dostępny sygnał już przy pierwszych iteracjach,
a wolne i umiarkowane LR prowadzą do tego samego minimum.
Powyżej LR=0.2 następuje gwałtowny rozpad modelu (zbieżny z
zachowaniem na pełnym zbiorze), jednak próg niestabilności jest
wyraźnie szerszy — model łatwiej „wystrzeluje" ponad płytką
hiperpowierzchnię błędu.

## Testy parametrów logiki rozmytej

Bezsprzecznie najlepszymi wynikami cechowała się logika rozmyta wykorzystująca c-means
oraz purity_threshold.

Analizowanymi dla logiki rozmytej parametrami są liczba klastrów
(`n_clusters`) próg czystości reguł (`purity_threshold`),
oraz współczynnik rozmycia. Ich wpływ był badany
na tym samym zbiorze danych, korzystając z 4 wybranych wcześniej cech.

### Liczba klastrów

Parametr ten decyduje o ziarnistości podziału przestrzeni cech.
Testowano wartości od 10 do 100. Zbyt mała liczba klastrów prowadzi
do powstawania dużych, niejednorodnych grup – reguły mają niską czystość i są
odrzucane, co skutkuje małą liczbą reguł i słabą generalizacją. Zbyt duża liczba
klastrów powoduje fragmentację danych, wiele klastrów nie spełnia progu czystości
lub ma zbyt małe wsparcie, a model staje się podatny na szum.
Początkowo wybraną wartością do pierwszych testów zostało `n_clusters=50`

### Próg czystości reguł

Im wyższy próg, tym bardziej rygorystyczne reguły – klasyfikator
ufa tylko bardzo jednorodnym obszarom. Niski próg zwiększa liczbę
reguł, ale wprowadza reguły o słabszej pewności,
co może obniżyć precyzję. Początkowo testowaną wartością było
`purity_threshold=0.7`

### Współczynnik rozmycia

Zgodnie z literaturą i wstępnymi testami wartość m = 2.0 zapewnia dobrą równowagę
między ostrością a rozmyciem przynależności. Zbyt małe m (bliskie 1) upodabnia
algorytm do twardego k-średnich, tracąc zdolność modelowania niepewności, natomiast
zbyt duże m prowadzi do prawie jednorodnych przynależności i degeneracji centroidów.
W początkowych eksperymentach użyto `m=2.0`.

### Proces analizy parametrów

Na podstawie wcześniej dobranych przykładowych parametrów przeprowadzono testy,
zmieniając ich wartości w zakresach:

- purity - 0.5 - 1
- n_clusters - 10 - 100
- m - 1 - 3

![First iter n_clusters](../plots/fcmp_1_n.png)
![First iter m param](../plots/fcmp_1_m.png)
![First iter purity](../plots/fcmp_1_p.png)

Wyniki pierwszej serii testów parametrów wykazały wartość testów
purity w szerszym zakresie, który potem rozszerzono do 0 - 1.

Tą metodą analizy opracowano finalne parametry.

### Końcowe parametry

Dalsze testy prowadzone tą samą metodą pokazały, że najlepsze wyniki, wynoszące
odpowiednio **F1 score = 0.8300** oraz **Accuracy = 0.8086** uzyskano dla wartości

- m = 2.9
- n = 60
- p = 0.5

![Last iter n_clusters](../plots/fcmp_5_n.png)
![Last iter m param](../plots/fcmp_5_m.png)
![Last iter purity](../plots/fcmp_5_p.png)

## Wnioski

<div style="background:#5a010155; border-left:4px solid #f9a825; border-radius:6px; padding:14px; margin:8px 0;">

⚠️ **Tu jest jakieś wnioski, ale trzeba je jeszcze przejrzeć**

### Uczenie maszynowe — pełny zbiór cech

Modele oparte na drzewach decyzyjnych (Random Forest, Decision Tree, HistGradientBoosting)
osiągają na pełnym zbiorze cech wyjątkowo wysokie wyniki: **Accuracy = 98,5%**, **F1 = 0,9615**.
Wskazuje to, że przy dostępności wszystkich cech klinicznych klasyfikacja typów depresji jest
zadaniem bardzo dobrze separowalnym dla modeli nieliniowych.
Regresja logistyczna ze względu na swoje liniowe ograniczenia okazała się praktycznie bezużyteczna
— nawet przy optymalnym hiperparametrze C osiąga jedynie 62,5% dokładności, co potwierdza
silnie nieliniową naturę badanego problemu.

Optymalne hiperparametry dla pełnego zbioru:

- **Random Forest:** `n_estimators ≥ 100`, `max_depth ≥ 20`
- **Decision Tree:** `max_depth ≥ 20`
- **HistGradientBoosting:** `learning_rate = 0.01`

### Uczenie maszynowe — ograniczony zbiór 4 cech

Przy redukcji do czterech dyskretnych cech identycznych z tymi używanymi przez system
logiki rozmytej, modele drzewowe osiągają pułap **Accuracy = 82,5%**, **F1 = 0,6814**,
a modele stabilizują się już przy niewielkich głębokościach i liczbach estymatorów.
Znaczna różnica względem pełnego zbioru (ok. 16 p.p. w Accuracy, ponad 28 p.p. w F1)
potwierdza, że barierą jest **ubóstwo informacyjne 4 dyskretnych cech**, a nie zdolność
modelu do ich eksploatacji — dodatkowe strojenie hiperparametrów nie przełamuje tego sufitu.
Regresja logistyczna w tym wariancie osiąga jedynie 43% dokładności i jest modelem
praktycznie niezdatnym do użycia.

### Porównanie modeli ML z logiką rozmytą (4 cechy)

| Metoda | Accuracy | F1 Score |
| :--- | :---: | :---: |
| ML (RF / DT / HGB, 4 cechy) | 0.8250 | 0.6814 |
| FCM + purity threshold | 0.8086 | **0.8300** |
| ML — pełny zbiór cech | **0.9850** | **0.9615** |

Na tym samym, ograniczonym zbiorze cech FCM z progiem czystości uzyskuje istotnie wyższy
wynik F1 (0.83 vs 0.68) niż najlepsze modele uczenia maszynowego, przy zbliżonej dokładności.
Oznacza to, że rozmyte klasterowanie lepiej radzi sobie z rzadko reprezentowanymi klasami
depresji na dyskretnej i ubogiej przestrzeni cech — co jest szczególnie istotne z uwagi na
przyjętą metrykę (F1 uwzględnia false-positive dla klas mniejszościowych).

Uczenie maszynowe dominuje jednoznacznie dopiero po udostępnieniu pełnego zbioru
cech klinicznych, gdzie jego przewaga jest bezsprzeczna (F1 0.96 vs 0.83).

### Podsumowanie

Wybór metody powinien być uzależniony od dostępności danych:
przy ograniczonym, dyskretnym zbiorze cech **logika rozmyta** (FCM + purity threshold)
zapewnia lepszą zdolność generalizacji na klasach mniejszościowych;
przy pełnych danych klinicznych **modele zespołowe uczenia maszynowego** (Random Forest,
HistGradientBoosting) są metodą preferowaną ze względu na znacznie wyższe metryki i
prostotę konfiguracji.

</div>

## Literatura

- <https://youtu.be/X7co6-U4BJY?si=IUTzW6A2S6wHReGo>
- <https://www.sciencedirect.com/science/article/pii/S0031320311003001>
- <https://informatica.vu.lt/journal/INFORMATICA/article/1124/read#j_info1224_ref_030>
- <https://www.jstage.jst.go.jp/article/softscis/2006/0/2006_0_1901/_pdf/-char/ja#2#2>

**Uczenie maszynowe w klasyfikacji zdrowia psychicznego**

- Bader, M., Abdelwanis, M., Maalouf, M., Jelinek, H.F. (2024). *Detecting depression severity using weighted random forest and oxidative stress biomarkers.* Scientific Reports, 14, Article 16328. <https://doi.org/10.1038/s41598-024-67251-y>
- Liang, L., Wang, Y., Ma, H., Zhang, R., Liu, R., Zhu, R., Zheng, Z., Zhang, X., Wang, F. (2024). *Enhanced classification and severity prediction of major depressive disorder using acoustic features and machine learning.* Frontiers in Psychiatry, 15, Article 1422020. <https://doi.org/10.3389/fpsyt.2024.1422020>

**Modele zespołowe i gradient boosting**

- Emami, S., Martínez-Muñoz, G. (2024). *Condensed-gradient boosting.* International Journal of Machine Learning and Cybernetics. <https://doi.org/10.1007/s13042-024-02279-0>

**Metryki ewaluacji i niezrównoważone klasy**

- Sujon, K.M., Hassan, R., Choi, K., Samad, M.A. (2025). *Accuracy, precision, recall, F1-score, or MCC? Empirical evidence from advanced statistics, ML, and XAI for evaluating business predictive models.* Journal of Big Data. <https://doi.org/10.1186/s40537-025-01313-4>

**Fuzzy C-Means**

- Demirhan, H. (2025). *Mixed fuzzy C-means clustering.* Information Sciences, 690, Article 121528. <https://doi.org/10.1016/j.ins.2024.121528>
- Zhang, L., Song, D., Qiu, H., Ye, L., Xu, Z. (2024). *Fuzzy C-means clustering algorithm applied in computed tomography images of patients with intracranial hemorrhage.* Frontiers in Neuroinformatics, 18, Article 1440304. <https://doi.org/10.3389/fninf.2024.1440304>

**Logika rozmyta w systemach wspomagania decyzji medycznych**

- Srivastava, A., Bhanot, D., Jasim, L.H., Varshney, N., Patil, V. (2025). *Advancements in fuzzy logic applications for diagnostic decision support systems in healthcare.* Fuzzy Information and Engineering, 17(3), 284–297. <https://doi.org/10.26599/FIE.2025.9270064>
- Hudon, A. (2025). *A hybrid fuzzy logic–Random Forest model to predict psychiatric treatment order outcomes: an interpretable tool for legal decision support.* Frontiers in Artificial Intelligence, 8, Article 1606250. <https://doi.org/10.3389/frai.2025.1606250>
