package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.platform.Platform;

public record PlatformDto(
        Long id,
        String name
) {
    public PlatformDto(Platform platform) {
        this(platform.getId(), platform.getName());
    }
}
