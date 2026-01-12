package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.validation.EnumNamePattern;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SaveMyGameDto(
        @NotNull(message = "id do jogo é obrigatório")
        Long gameId,
        @NotNull(message = "id da plataforma é obrigatório")
        Long platformId,
        @NotNull(message = "id da origem/loja é obrigatório")
        Long sourceId,
        @EnumNamePattern(regexp = "NOT_PLAYED|PLAYING|COMPLETED|ABANDONED|ON_HOLD|WISHLIST")
        Status status
) {
}
