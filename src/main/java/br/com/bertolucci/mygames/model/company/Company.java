package br.com.bertolucci.mygames.model.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Company(SaveCompanyDto dto) {
        this.name = dto.name();
    }

    public void update(UpdateCompanyDto dto) {
        this.name = dto.name();
    }
}