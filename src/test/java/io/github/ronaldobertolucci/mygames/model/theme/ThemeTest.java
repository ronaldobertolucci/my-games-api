package io.github.ronaldobertolucci.mygames.model.theme;

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
class ThemeTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ThemeRepository repository;

    @Test
    void deveSalvarTemaComNome() {
        // given
        var theme = new Theme();
        theme.setName("Theme name ");
        em.persist(theme);

        //then
        Theme loadedTheme = repository.getReferenceById(theme.getId());
        assertEquals("theme name", loadedTheme.getName());
    }

    @Test
    void deveAtualizarTemaComNome() {
        // given
        var theme = new Theme();
        theme.setName("Theme name ");
        em.persist(theme);

        Theme loadedTheme = repository.getReferenceById(theme.getId());

        //then
        loadedTheme.update(new UpdateThemeDto(loadedTheme.getId(), "New name"));
        assertEquals("new name", loadedTheme.getName());
    }
}
