package io.github.ronaldobertolucci.mygames.model.platform;


public record PlatformDto(
        Long id,
        String name
) {
    public PlatformDto(Platform platform) {
        this(platform.getId(), platform.getName());
    }
}
