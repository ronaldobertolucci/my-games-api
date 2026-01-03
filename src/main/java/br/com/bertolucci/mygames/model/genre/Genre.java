package br.com.bertolucci.mygames.model.genre;

import br.com.bertolucci.mygames.model.company.SaveCompanyDto;
import br.com.bertolucci.mygames.model.company.UpdateCompanyDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

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