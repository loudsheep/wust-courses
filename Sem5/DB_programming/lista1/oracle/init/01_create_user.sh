#!/bin/bash
# Executed by the Oracle lite image on EVERY container start (scripts/startup;
# the lite image does not run scripts/setup). Must be idempotent.
# Creates pbduser in the pluggable database FREEPDB1 and syncs its password with .env.
sqlplus -s / as sysdba <<SQL
WHENEVER SQLERROR EXIT FAILURE
ALTER SESSION SET CONTAINER = FREEPDB1;
DECLARE
  n NUMBER;
BEGIN
  SELECT COUNT(*) INTO n FROM dba_users WHERE username = 'PBDUSER';
  IF n = 0 THEN
    EXECUTE IMMEDIATE 'CREATE USER pbduser IDENTIFIED BY "${PBD_PASSWORD}"';
  END IF;
END;
/
ALTER USER pbduser IDENTIFIED BY "${PBD_PASSWORD}" ACCOUNT UNLOCK;
GRANT CONNECT, RESOURCE, CREATE VIEW TO pbduser;
GRANT UNLIMITED TABLESPACE TO pbduser;
EXIT;
SQL
