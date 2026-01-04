CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description text,
    released_at date,
    company_id bigint NOT NULL,

    UNIQUE (title, company_id),
    FOREIGN KEY (company_id)
        REFERENCES companies (id)
        ON DELETE CASCADE
);
