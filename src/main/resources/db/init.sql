DROP TABLE IF EXISTS health;

CREATE TABLE health
(
    id INT PRIMARY KEY,
    up BOOLEAN
);

INSERT INTO health
    (id, up)
VALUES (1, true);

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id SERIAL PRIMARY KEY,
    name VARCHAR
);

CREATE UNIQUE INDEX username_idx ON users(name);