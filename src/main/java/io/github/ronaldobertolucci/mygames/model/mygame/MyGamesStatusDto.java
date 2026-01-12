package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.validation.EnumNamePattern;
import jakarta.validation.constraints.NotNull;

public record MyGamesStatusDto(
        @NotNull
        @EnumNamePattern(regexp = "NOT_PLAYED|PLAYING|COMPLETED|ABANDONED|ON_HOLD|WISHLIST")
        Status status
) {
}
