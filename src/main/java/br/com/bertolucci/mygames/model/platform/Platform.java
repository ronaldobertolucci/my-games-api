package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.platform.SavePlatformDto;
import br.com.bertolucci.mygames.model.platform.UpdatePlatformDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@NoArgsConstructor
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Platform(SavePlatformDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(UpdatePlatformDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}