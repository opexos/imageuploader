set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER images PASSWORD 'images';
    CREATE SCHEMA images;
    ALTER SCHEMA images OWNER TO images;
    ALTER USER images SET search_path = images;
EOSQL
