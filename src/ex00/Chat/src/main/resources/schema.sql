CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE chatroom (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    owner INTEGER,
    FOREIGN KEY (owner) REFERENCES "user"(id)
);

CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    author INTEGER NOT NULL,
    room INTEGER NOT NULL,
    text TEXT NOT NULL,
    dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (author) REFERENCES "user"(id),
    FOREIGN KEY (room) REFERENCES chatroom(id)
);