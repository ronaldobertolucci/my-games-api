package io.github.ronaldobertolucci.mygames.service.source;

import io.github.ronaldobertolucci.mygames.model.source.SourceDto;
import io.github.ronaldobertolucci.mygames.model.source.SaveSourceDto;
import io.github.ronaldobertolucci.mygames.model.source.UpdateSourceDto;
import io.github.ronaldobertolucci.mygames.service.source.SourceService;
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
class SourceServiceTest {

    @Autowired
    private SourceService sourceService;

    @Test
    @Transactional
    void deveListarTodos() {
        String name = "Source name";
        SourceDto sourceDto = sourceService.save(new SaveSourceDto(name));

        List<SourceDto> sources = sourceService.findAll();

        assertEquals(name.toLowerCase().trim(), sources.getFirst().name());
        assertEquals(1, sources.size());
    }

    @Test
    @Transactional
    void deveDetalharExistente() {
        String name = "Source name";
        SourceDto saved = sourceService.save(new SaveSourceDto(name));

        SourceDto detailed = sourceService.detail(saved.id());

        assertEquals(name.toLowerCase().trim(), detailed.name());
    }

    @Test
    @Transactional
    void deveSalvarComOsDadosNecessarios() {
        String name = "Source name";
        SourceDto sourceDto = sourceService.save(new SaveSourceDto(name));

        assertEquals(name.toLowerCase().trim(), sourceDto.name());
    }

    @Test
    @Transactional
    void deveAtualizarComOsDadosNecessarios() {
        SourceDto savedDto = sourceService.save(new SaveSourceDto("Source name"));

        String name = " New Source name";
        SourceDto updatedDto = sourceService.update(new UpdateSourceDto(savedDto.id(), name));

        assertEquals(name.toLowerCase().trim(), updatedDto.name());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String name = "Source name";
        SourceDto sourceDto = sourceService.save(new SaveSourceDto(name));

        assertDoesNotThrow(() -> sourceService.delete(sourceDto.id()));
    }

}