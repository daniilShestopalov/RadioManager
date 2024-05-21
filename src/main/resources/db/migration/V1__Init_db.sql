CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TYPE approval_status AS ENUM ('APPROVED', 'PENDING', 'REJECTED');

CREATE TYPE status AS ENUM ('OCCUPIED', 'AVAILABLE');

CREATE TABLE "user" (
                        id SERIAL PRIMARY KEY,
                        login VARCHAR(255) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        surname VARCHAR(50) NOT NULL,
                        balance DOUBLE PRECISION  NOT NULL,
                        role user_role NOT NULL
);

CREATE TABLE audio_recording (
                                 id SERIAL PRIMARY KEY,
                                 user_id INTEGER NOT NULL,
                                 duration DOUBLE PRECISION NOT NULL,
                                 cost DOUBLE PRECISION NOT NULL,
                                 approval_status approval_status NOT NULL,
                                 file_path VARCHAR(255) NOT NULL,
                                 FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE broadcast_slot (
                                id SERIAL PRIMARY KEY,
                                start_time TIMESTAMP NOT NULL,
                                end_time TIMESTAMP NOT NULL,
                                status status NOT NULL
);

CREATE TABLE placement (
                           id SERIAL PRIMARY KEY,
                           slot_id INTEGER NOT NULL,
                           audio_id INTEGER NOT NULL,
                           placement_date TIMESTAMP NOT NULL,
                           FOREIGN KEY (slot_id) REFERENCES broadcast_slot (id) ON DELETE CASCADE,
                           FOREIGN KEY (audio_id) REFERENCES audio_recording (id) ON DELETE CASCADE
);

CREATE TABLE transaction (
                             id SERIAL PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             admin_id INTEGER NOT NULL,
                             amount DOUBLE PRECISION NOT NULL,
                             transaction_date TIMESTAMP NOT NULL,
                             description TEXT,
                             FOREIGN KEY (user_id) REFERENCES "user" (id),
                             FOREIGN KEY (admin_id) REFERENCES "user" (id)
);

