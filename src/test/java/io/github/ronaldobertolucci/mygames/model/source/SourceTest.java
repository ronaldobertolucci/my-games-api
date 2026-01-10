package io.github.ronaldobertolucci.mygames.model.source;

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
class SourceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private SourceRepository repository;

    @Test
    void deveSalvarLojaComNome() {
        // given
        var source = new Source();
        source.setName("Source name ");
        em.persist(source);

        //then
        Source loadedSource = repository.getReferenceById(source.getId());
        assertEquals("source name", loadedSource.getName());
    }

    @Test
    void deveAtualizarLojaComNome() {
        // given
        var source = new Source();
        source.setName("Source name ");
        em.persist(source);

        Source loadedSource = repository.getReferenceById(source.getId());

        //then
        loadedSource.update(new UpdateSourceDto(loadedSource.getId(), "New name"));
        assertEquals("new name", loadedSource.getName());
    }
}
