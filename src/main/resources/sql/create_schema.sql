CREATE TABLE IF NOT EXISTS applications (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    filename TEXT NOT NULL,
    `type` TEXT NOT NULL,
    pid TEXT
);