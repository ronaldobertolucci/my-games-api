package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.platform.Platform;
import br.com.bertolucci.mygames.model.platform.PlatformRepository;
import br.com.bertolucci.mygames.model.platform.UpdatePlatformDto;
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
class PlatformTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlatformRepository repository;

    @Test
    void deveSalvarCompanhiaComNome() {
        // given
        var platform = new Platform();
        platform.setName("Platform name ");
        em.persist(platform);

        //then
        Platform loadedPlatform = repository.getReferenceById(platform.getId());
        assertEquals("platform name", loadedPlatform.getName());
    }

    @Test
    void deveAtualizarCompanhiaComNome() {
        // given
        var platform = new Platform();
        platform.setName("Platform name ");
        em.persist(platform);

        Platform loadedPlatform = repository.getReferenceById(platform.getId());

        //then
        loadedPlatform.update(new UpdatePlatformDto(loadedPlatform.getId(), "New name"));
        assertEquals("new name", loadedPlatform.getName());
    }
}
