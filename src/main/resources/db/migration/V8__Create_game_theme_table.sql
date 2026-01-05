CREATE TABLE IF NOT EXISTS game_theme (
    id SERIAL PRIMARY KEY,
    game_id bigint NOT NULL,
    theme_id bigint NOT NULL,

    UNIQUE (game_id, theme_id),
    FOREIGN KEY (game_id)
        REFERENCES games (id)
        ON DELETE CASCADE,
    FOREIGN KEY (theme_id)
        REFERENCES themes (id)
            ON DELETE CASCADE
);
