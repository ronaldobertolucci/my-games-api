package br.com.bertolucci.mygames.model.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CompanyTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CompanyRepository repository;

    @Test
    void deveSalvarCompanhiaComNome() {
        // given
        var company = new Company();
        company.setName("Company name");
        em.persist(company);

        //then
        Company loadedCompany = repository.getReferenceById(company.getId());
        assertEquals("Company name", loadedCompany.getName());
    }

    @Test
    void deveAtualizarCompanhiaComNome() {
        // given
        var company = new Company();
        company.setName("Company name");
        em.persist(company);

        Company loadedCompany = repository.getReferenceById(company.getId());

        //then
        loadedCompany.update(new UpdateCompanyDto(loadedCompany.getId(), "New name"));
        assertEquals("New name", loadedCompany.getName());
    }
}
