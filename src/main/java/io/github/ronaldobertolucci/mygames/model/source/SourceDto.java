package io.github.ronaldobertolucci.mygames.model.source;

public record SourceDto(
        Long id,
        String name
) {
    public SourceDto(Source source) {
        this(source.getId(), source.getName());
    }
}
