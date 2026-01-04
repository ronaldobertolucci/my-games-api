package io.github.ronaldobertolucci.mygames.model.store;

public record StoreDto(
        Long id,
        String name
) {
    public StoreDto(Store store) {
        this(store.getId(), store.getName());
    }
}
