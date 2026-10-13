# PBD Lista 1 - Koty (Oracle + PostgreSQL)

Local Oracle Free (lite) and PostgreSQL 18 in Docker, scripts for tasks 1 and 2.
Containers never start on their own (`restart: "no"`), you start them by hand.

## Layout

```
docker-compose.yml     oracle + postgres, named volumes
.env / .env.example    passwords and host ports (.env is gitignored)
oracle/init/           runs on every Oracle start (idempotent): creates pbduser in FREEPDB1
oracle/                00_drop.sql, 01_create.sql (zad 1), 02_insert.sql (zad 2)
postgres/              same three files, PostgreSQL dialect
```

## Start / stop

```bash
cp .env.example .env            # first time only, already done once
docker compose up -d
docker compose ps               # wait until both are "healthy"
                                # Oracle: first start takes a few minutes
docker compose stop             # stop, data kept
docker compose down -v          # stop and WIPE all data (volumes)
```

`pbduser` is created by `oracle/init/01_create_user.sh`, which the lite image runs on every start
(it skips creation if the user exists and re-syncs the password with `.env`).

## DBeaver connections

| | Oracle | PostgreSQL |
|---|---|---|
| Host | localhost | localhost |
| Port | 1521 | 5432 |
| Database | Service name: `FREEPDB1` | `pbd` |
| User | `pbduser` | `pbduser` |
| Password | `PBD_PASSWORD` from `.env` | `PBD_PASSWORD` from `.env` |

DBeaver downloads the JDBC driver on first connect.
Check the Oracle service name if the connection fails:

```bash
docker exec pbd-oracle bash -c 'lsnrctl status | grep -i service'
```

## Run the scripts

Order: `00_drop.sql` (optional, for re-runs), `01_create.sql`, `02_insert.sql`.

In DBeaver: open the file with the right connection active, then **Alt+X** (Execute script), not Ctrl+Enter (that runs one statement).

From the CLI (headless re-runs):

```bash
# PostgreSQL
docker exec -i pbd-postgres psql -U pbduser -d pbd -v ON_ERROR_STOP=1 < postgres/01_create.sql
docker exec -i pbd-postgres psql -U pbduser -d pbd -v ON_ERROR_STOP=1 < postgres/02_insert.sql

# Oracle (password from .env)
set -a; . ./.env; set +a
docker exec -i pbd-oracle sqlplus -s pbduser/"$PBD_PASSWORD"@//localhost:1521/FREEPDB1 < oracle/01_create.sql
docker exec -i pbd-oracle sqlplus -s pbduser/"$PBD_PASSWORD"@//localhost:1521/FREEPDB1 < oracle/02_insert.sql
```

## Quick check after loading

```sql
SELECT 'Funkcje', COUNT(*) FROM Funkcje UNION ALL      -- 8
SELECT 'Bandy', COUNT(*) FROM Bandy UNION ALL           -- 5
SELECT 'Wrogowie', COUNT(*) FROM Wrogowie UNION ALL     -- 10
SELECT 'Kocury', COUNT(*) FROM Kocury UNION ALL         -- 18
SELECT 'Wrogowie_kocurow', COUNT(*) FROM Wrogowie_kocurow; -- 18
```

## Notes on the scripts

- Constraints are inline in `CREATE TABLE`. Table level only where it cannot be otherwise:
  cross-column CHECK on `Funkcje`, composite PK on `Wrogowie_kocurow`.
- One `ALTER TABLE`: `Bandy.szef_bandy -> Kocury.pseudo`, circular with `Kocury.nr_bandy -> Bandy`.
- Because of that FK, inserts load `Bandy` with `szef_bandy = NULL` and fill it with `UPDATE` after `Kocury`.
- Oracle `00_drop.sql` uses `DROP TABLE IF EXISTS` (Oracle 23ai).
