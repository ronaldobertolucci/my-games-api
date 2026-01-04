package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.store.StoreDto;
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
