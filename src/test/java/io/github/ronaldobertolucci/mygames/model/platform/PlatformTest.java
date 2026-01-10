package io.github.ronaldobertolucci.mygames.model.platform;

import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.source.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PlatformTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    void deveSalvarPlataformaComDadosCorretos() {
        // given
        var platform = new Platform();
        platform.setName("Platform name ");
        em.persist(platform);

        //then
        Platform loadedPlatform = platformRepository.getReferenceById(platform.getId());
        assertEquals("platform name", loadedPlatform.getName());
    }

    @Test
    void deveAtualizarPlataformaComNome() {
        // given
        var platform = new Platform();
        platform.setName("Platform name ");
        em.persist(platform);

        Platform loadedPlatform = platformRepository.getReferenceById(platform.getId());

        //then
        loadedPlatform.update(new UpdatePlatformDto(loadedPlatform.getId(), "New name "));
        assertEquals("new name", loadedPlatform.getName());
    }
}
