CREATE TABLE IF NOT EXISTS my_games (
    id SERIAL PRIMARY KEY,
    user_id bigint NOT NULL,
    game_id bigint NOT NULL,
    platform_id bigint NOT NULL,
    source_id bigint NOT NULL,
    status VARCHAR(50) NOT NULL,

    UNIQUE (user_id, game_id, platform_id, source_id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (game_id)
        REFERENCES games (id)
            ON DELETE CASCADE,
    FOREIGN KEY (platform_id)
        REFERENCES platforms (id)
            ON DELETE CASCADE,
    FOREIGN KEY (source_id)
        REFERENCES sources (id)
            ON DELETE CASCADE
);
