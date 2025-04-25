--liquibase formatted sql
--changeset test-data:1 context:test

INSERT INTO Users (login, password) VALUES
    ('test@example.com', '$2a$10$xJwL5vYRXZ6F6rBVi7z.aeIYQ/7qCIXmzNfJQdJQHPN9z1YKzLQe.');

INSERT INTO Locations (name, user_id, latitude, longitude) VALUES
    ('Москва', 1, 55.7558, 37.6176);

INSERT INTO Sessions (user_id, expires_at) VALUES
    (1, CURRENT_TIMESTAMP + INTERVAL '1' HOUR);