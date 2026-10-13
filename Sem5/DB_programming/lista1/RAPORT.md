# PBD - Lista projektowa 1 - Koty (Oracle, PostgreSQL)

## 1. Środowisko

- Oracle AI Database 26ai Free (obraz `lite`) i PostgreSQL 18, oba w Dockerze (Docker Compose).
- Klient: DBeaver (zamiast SQL Developer i pgAdmin).
- Skrypty: `oracle/` i `postgres/` (`01_create.sql`, `02_insert.sql`).

## 2. Zadanie 1 - schemat

Pięć tabel: `Funkcje`, `Wrogowie`, `Bandy`, `Kocury`, `Wrogowie_kocurow`. Ograniczenia kolumnowe zdefiniowane w `CREATE TABLE`.

Różnice Oracle i PostgreSQL:

| Oracle | PostgreSQL |
|---|---|
| `VARCHAR2(n)` | `VARCHAR(n)` |
| `NUMBER(n)` | `NUMERIC(n)` |
| `DEFAULT SYSDATE` | `DEFAULT CURRENT_DATE` |
| `DATE` zawiera czas | `DATE` bez czasu |
| nazwy w górnym rejestrze | nazwy w dolnym rejestrze |
| `DROP TABLE ... CASCADE CONSTRAINTS PURGE` | `DROP TABLE ... CASCADE` |
| `COMMIT` potrzebny | tryb autocommit |

## 3. Zadanie 2 - dane

Kolejność: `Funkcje`, `Bandy` (z `szef_bandy = NULL`), `Wrogowie`, `Kocury` (szef przed podwładnym), `UPDATE Bandy`, `Wrogowie_kocurow`.
Weryfikacja liczności w obu bazach: 8 / 5 / 10 / 18 / 18.

## 4. Problemy i modyfikacje

- Cykl kluczy obcych `Bandy.szef_bandy` i `Kocury.nr_bandy`: jedyny `ALTER TABLE` (dodanie FK po utworzeniu `Kocury`).
- `Kocury.szef` wskazuje na tę samą tabelę: kolejność `INSERT` według hierarchii.
- Oracle: `ORA-02438`, `CHECK max_myszy >= min_myszy` musi być na poziomie tabeli (w obu bazach tak samo).
- Obraz `lite` nie uruchamia skryptów `setup`: użytkownik tworzony skryptem `startup`.
- Użyto DBeaver zamiast SQL Developer i pgAdmin (działa z oboma SZBD).

## 5. Podsumowanie i ocena SZBD

Oba skrypty zostały uruchomione bez błędów, schemat i dane są identyczne.
PostgreSQL oceniam lepiej: jest lekki, startuje w kilka sekund, ma standardowe typy i prostszą składnię a doświadczenie z nim ułatwia pracę. Oracle jest ciężki (duży obraz, kilka minut startu) i zamknięty (i został stworzony przez Oracle)

## 6. Użycie narzędzi AI

Claude (Anthropic) użyto do przygotowania `docker-compose.yml`, skryptów SQL i szkicu raportu. Wyniki zweryfikowano uruchomieniem skryptów w obu bazach.
