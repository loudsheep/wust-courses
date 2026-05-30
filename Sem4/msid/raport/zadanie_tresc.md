# Zadanie laboratoryjne (praca grupowa – grupy dwuosobowe)

## Ogólny opis zadania

Zadanie polega na porównaniu dwóch rodzajów systemów decyzyjnych:

- Wykorzystujących weak AI (znanych również jako rule-based AI) działających na bazie logiki rozmytej. (fuzzy logic)

- Inteligentnych, wykorzystujących techniki uczenia maszynowego/głębokiego (ML/DL).

## Opis szczegółowy

W celu porównania w/w systemów należy odpowiednio dobrać problem/zbiór uczący (dataset). Może to być np. zbiór danych o charakterze medycznym (można znaleźć dataset dotyczących pacjentów diagnozowanych pod kątem cukrzycy, np. Diabetes Dataset), ale także dowolny inny, który przechowuje nieprzesadnie dużo atrybutów (powiedzmy do 10).

Następnie w języku Python należy napisać oprogramowanie, które korzysta z modułów:

- scikit-learn (wraz z modułami pandas, numpy, scipy, itp.)

- scikit-fuzzy/fuzzylogic/amfis/simpful

Porównanie należy prowadzić pod kątem sprawdzenia poprawności działania obu metodologii.

Dla metody fuzzy logic należy dodatkowo sprawdzić jaki wpływ na poprawność działania ma np.:

- liczba atrybutów branych pod uwagę przy formułowaniu reguł,

- liczbę funkcji przynależności,

- rodzaj/typ funkcji przynależności (np. trimf vs. trampf vs. gaussmf).

Z kolei, dla metod ML można dodatkowo sprawdzić wpływ dobranego modelu:

- Logistic Regression, Linear Regression,

- Decision Tree,

- Random Forest.

Jednym z zadań jest zastanowienie się nad 2-3 metrykami, które pozwolą rzeczywiście porównać te dwie metodologie (może to być np. czas uzyskania odpowiedzi, ale warto się zastanowić nad 2-3 metrykami pozwalającymi porównać dokładność ich działania).

## Zaliczenie przedmiotu

Zaliczenie przedmiotu będzie wymagało demonstracji działania oprogramownia oraz sporządzenia skondensowanego raportu przedstawiającego założenia, opisującego przeprowadzone testy, przezentującego wyniki, dyskusję oraz wnioski.

