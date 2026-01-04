package io.github.ronaldobertolucci.mygames.model.theme;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "themes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Theme(SaveThemeDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(UpdateThemeDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}