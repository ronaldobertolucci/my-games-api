package io.github.ronaldobertolucci.mygames.model.platform;

import io.github.ronaldobertolucci.mygames.model.store.StoreDto;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PlatformDto(
        Long id,
        String name,
        StoreDto store
) {
    public PlatformDto(Platform platform) {
        this(platform.getId(), platform.getName(), new StoreDto(platform.getStore()));
    }
}
