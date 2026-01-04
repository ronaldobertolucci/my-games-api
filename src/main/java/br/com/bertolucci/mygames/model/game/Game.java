package br.com.bertolucci.mygames.model.game;

import br.com.bertolucci.mygames.model.company.Company;
import br.com.bertolucci.mygames.model.genre.Genre;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"title", "company"})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(name = "released_at")
    private LocalDate releasedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    public void setTitle(String title) {
        this.title = title.toLowerCase().trim();
    }

    public void setDescription(String description) {
        this.description = description.toLowerCase().trim();
    }
}