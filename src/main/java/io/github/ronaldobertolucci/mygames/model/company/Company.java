package io.github.ronaldobertolucci.mygames.model.company;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Company(SaveCompanyDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void update(UpdateCompanyDto dto) {
        this.name = normalizeName(dto.name());
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    private String normalizeName(String name) {
        return name.toLowerCase().trim();
    }
}