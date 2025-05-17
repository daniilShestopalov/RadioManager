ALTER TYPE user_role ADD VALUE 'RADIO_REPRESENTATIVE';
ALTER TYPE user_role RENAME VALUE 'USER' TO 'ADVERTISER';

CREATE TABLE city (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        region VARCHAR(100) NOT NULL,
                        UNIQUE (name, region)
);

CREATE TABLE radio_station (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(255) UNIQUE NOT NULL,
                                frequency NUMERIC(4,1) NOT NULL CHECK (frequency > 0),
                                city_id INT NOT NULL REFERENCES city(id) ON DELETE CASCADE,
                                representative_id INT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE
);

-- Очистка таблиц перед изменением структуры
TRUNCATE TABLE placement CASCADE;
TRUNCATE TABLE broadcast_slot CASCADE;

ALTER TABLE broadcast_slot ADD COLUMN radio_station_id INT NOT NULL REFERENCES radio_station(id) ON DELETE CASCADE;