package br.com.bertolucci.mygames.model.genre;

import br.com.bertolucci.mygames.model.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Game> games = new HashSet<>();

    public Genre(SaveGenreDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(UpdateGenreDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}