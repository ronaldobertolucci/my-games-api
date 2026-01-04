package io.github.ronaldobertolucci.mygames.model.platform;

import io.github.ronaldobertolucci.mygames.model.store.Store;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"name", "store"})
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;

    public Platform(SavePlatformDto dto) {
        this.name = normalizeName(dto.name());
    }

    public Platform(String name, Store store) {
        this.name = normalizeName(name);
        this.store = store;
    }

    public void update(UpdatePlatformDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(String name, Store store) {
        this.name = normalizeName(name);
        this.store = store;
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}