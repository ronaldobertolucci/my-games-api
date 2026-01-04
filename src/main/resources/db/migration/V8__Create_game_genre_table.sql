CREATE TABLE IF NOT EXISTS game_genre (
    id SERIAL PRIMARY KEY,
    game_id bigint NOT NULL,
    genre_id bigint NOT NULL,

    UNIQUE (game_id, genre_id),
    FOREIGN KEY (game_id)
        REFERENCES games (id)
        ON DELETE CASCADE,
    FOREIGN KEY (genre_id)
        REFERENCES genres (id)
            ON DELETE CASCADE
);
