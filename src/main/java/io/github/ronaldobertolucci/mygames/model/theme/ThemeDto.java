package io.github.ronaldobertolucci.mygames.model.theme;


public record ThemeDto(
        Long id,
        String name
) {
    public ThemeDto(Theme theme) {
        this(theme.getId(), theme.getName());
    }
}
