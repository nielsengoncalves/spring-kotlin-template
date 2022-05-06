CREATE TABLE users
(
    id                      UUID PRIMARY KEY,
    github_username         TEXT      NOT NULL UNIQUE,
    name                    TEXT      NULL,
    company                 TEXT      NULL,
    location                TEXT      NULL,
    bio                     TEXT      NULL,
    is_available_for_hiring BOOLEAN   NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);
