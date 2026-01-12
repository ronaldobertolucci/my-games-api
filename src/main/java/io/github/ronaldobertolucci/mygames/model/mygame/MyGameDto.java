package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.platform.PlatformDto;
import io.github.ronaldobertolucci.mygames.model.source.SourceDto;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MyGameDto(
        Long id,
        Long userId,
        GameDto game,
        PlatformDto platform,
        SourceDto source,
        Status status
) {
    public MyGameDto(MyGame myGame) {
        this(myGame.getId(), myGame.getUser().getId(), new GameDto(myGame.getGame()),
                new PlatformDto(myGame.getPlatform()), new SourceDto(myGame.getSource()),
                myGame.getStatus());
    }
}
