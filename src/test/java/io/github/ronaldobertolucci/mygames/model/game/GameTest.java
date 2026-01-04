package io.github.ronaldobertolucci.mygames.model.game;

import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GameTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void deveSalvarJogoComDadosNecessarios() {
        // given
        var company = new Company();
        company.setName("company name");
        em.persist(company);

        Company loadedCompany = companyRepository.getReferenceById(company.getId());

        var game = new Game();
        game.setTitle("Game title ");
        game.setCompany(loadedCompany);
        em.persist(game);

        //then
        Game loadedGame = gameRepository.getReferenceById(game.getId());
        assertEquals("game title", loadedGame.getTitle());
        assertEquals(loadedCompany.getId(), loadedGame.getCompany().getId());
        assertNull(loadedGame.getDescription());
        assertNull(loadedGame.getReleasedAt());
        assertTrue(loadedGame.getGenres().isEmpty());
    }

    @Test
    void deveFalharNoSalvamentoDeJogoSemTitulo() {
        // given
        var company = new Company();
        company.setName("company name");
        em.persist(company);

        Company loadedCompany = companyRepository.getReferenceById(company.getId());

        var game = new Game();
        game.setCompany(loadedCompany);
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> em.persist(game));
    }

    @Test
    void deveFalharNoSalvamentoDeJogoSemIdCompanhia() {
        var game = new Game();
        game.setTitle("Game title");
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> em.persist(game));
    }

}
