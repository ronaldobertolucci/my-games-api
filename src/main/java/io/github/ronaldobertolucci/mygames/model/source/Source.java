package io.github.ronaldobertolucci.mygames.model.source;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sources")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Source(SaveSourceDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(UpdateSourceDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}