set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER images PASSWORD 'images';
    CREATE SCHEMA images;
    ALTER SCHEMA images OWNER TO images;
    ALTER USER images SET search_path = images;
    CREATE TABLE images.image(
      id SERIAL PRIMARY KEY,
      original BYTEA,
      preview BYTEA,
      upload_date TIMESTAMP WITH TIME ZONE
    );
    ALTER TABLE images.image OWNER TO images;
EOSQL