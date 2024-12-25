#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE gira;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "gira" -f /docker-entrypoint-initdb.d/02-schema.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "gira" -f /docker-entrypoint-initdb.d/03-data.sql 