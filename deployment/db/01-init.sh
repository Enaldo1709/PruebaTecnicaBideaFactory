#!/bin/bash
set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';
  CREATE DATABASE $APP_DB_NAME;
  GRANT CONNECT ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
  
  \connect $APP_DB_NAME $POSTGRES_USER
  BEGIN;
    CREATE SCHEMA $APP_DB_SCHEMA;
    GRANT USAGE ON SCHEMA $APP_DB_SCHEMA TO $APP_DB_USER;

    CREATE TABLE IF NOT EXISTS $APP_DB_SCHEMA.house(
	    id SERIAL PRIMARY KEY NOT NULL,
	    house_id VARCHAR(32) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS $APP_DB_SCHEMA.user(
        id VARCHAR(32) PRIMARY KEY NOT NULL,
        name VARCHAR(32),
        lastname VARCHAR(32),
        age SMALLINT,
        phone_number VARCHAR(15)
    );

    CREATE TABLE IF NOT EXISTS $APP_DB_SCHEMA.book(
        id SERIAL PRIMARY KEY NOT NULL,
        user_id VARCHAR(32) NOT NULL,
        start_date TIMESTAMP NOT NULL,
        end_date TIMESTAMP NOT NULL,
        house_id INT NOT NULL,
        discount_code VARCHAR(32),
        
        CONSTRAINT fk_user
            FOREIGN KEY(user_id)
            REFERENCES $APP_DB_SCHEMA.user(id),
        
        CONSTRAINT fk_house
            FOREIGN KEY(house_id) 
            REFERENCES $APP_DB_SCHEMA.house(id)
        
    );

    CREATE INDEX idx_book_house_id ON $APP_DB_SCHEMA.book (house_id); 
    CREATE INDEX idx_book_user_id ON $APP_DB_SCHEMA.book (user_id);
    CREATE INDEX idx_house_house_id ON $APP_DB_SCHEMA.house (house_id);

    GRANT SELECT , INSERT ON TABLE $APP_DB_SCHEMA.book TO $APP_DB_USER;
    GRANT SELECT , INSERT ON TABLE $APP_DB_SCHEMA.house TO $APP_DB_USER;
    GRANT SELECT , INSERT ON TABLE $APP_DB_SCHEMA.user TO $APP_DB_USER;
    
    GRANT USAGE , SELECT ON SEQUENCE $APP_DB_SCHEMA.house_id_seq to $APP_DB_USER;

    GRANT USAGE , SELECT on SEQUENCE $APP_DB_SCHEMA.book_id_seq to $APP_DB_USER;
  COMMIT;
EOSQL