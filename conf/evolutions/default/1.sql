# --- !Ups

CREATE TABLE IF NOT EXISTS account(
    id_user SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL
);

INSERT INTO account(name, password)
VALUES('jacke', 'a92f6bdb75789bccc118adfcf704029aa58063c604bab4fcdd9cd126ef9b69af');

CREATE TABLE IF NOT EXISTS album(
    id_album SERIAL,
    name VARCHAR(255) NOT NULL,
    interpret VARCHAR(255) NOT NULL,
    fk_user INT NOT NULL REFERENCES account(id_user),
    UNIQUE (name,interpret,fk_user)
);

# --- !Downs

DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS account;

