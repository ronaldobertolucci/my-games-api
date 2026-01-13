package io.github.ronaldobertolucci.mygames.service.platform;

import io.github.ronaldobertolucci.mygames.model.platform.PlatformDto;
import io.github.ronaldobertolucci.mygames.model.platform.SavePlatformDto;
import io.github.ronaldobertolucci.mygames.model.platform.UpdatePlatformDto;
import io.github.ronaldobertolucci.mygames.service.platform.PlatformService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PlatformServiceTest {

    @Autowired
    private PlatformService platformService;

    @Test
    @Transactional
    void deveListarTodos() {
        String name = "Platform name";
        PlatformDto platformDto = platformService.save(new SavePlatformDto(name));

        List<PlatformDto> platforms = platformService.findAll();

        assertEquals(name.toLowerCase().trim(), platforms.getFirst().name());
        assertEquals(1, platforms.size());
    }

    @Test
    @Transactional
    void deveDetalharExistente() {
        String name = "Platform name";
        PlatformDto saved = platformService.save(new SavePlatformDto(name));

        PlatformDto detailed = platformService.detail(saved.id());

        assertEquals(name.toLowerCase().trim(), detailed.name());
    }

    @Test
    @Transactional
    void deveSalvarComOsDadosNecessarios() {
        String name = "Platform name";
        PlatformDto platformDto = platformService.save(new SavePlatformDto(name));

        assertEquals(name.toLowerCase().trim(), platformDto.name());
    }

    @Test
    @Transactional
    void deveAtualizarComOsDadosNecessarios() {
        PlatformDto savedDto = platformService.save(new SavePlatformDto("Platform name"));

        String name = " New Platform name";
        PlatformDto updatedDto = platformService.update(new UpdatePlatformDto(savedDto.id(), name));

        assertEquals(name.toLowerCase().trim(), updatedDto.name());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String name = "Platform name";
        PlatformDto platformDto = platformService.save(new SavePlatformDto(name));

        assertDoesNotThrow(() -> platformService.delete(platformDto.id()));
    }

}