package io.github.ronaldobertolucci.mygames.service.theme;

import io.github.ronaldobertolucci.mygames.model.theme.ThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.SaveThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.UpdateThemeDto;
import io.github.ronaldobertolucci.mygames.service.theme.ThemeService;
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
class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @Test
    @Transactional
    void deveListarTodos() {
        String name = "Theme name";
        ThemeDto themeDto = themeService.save(new SaveThemeDto(name));

        List<ThemeDto> themes = themeService.findAll();

        assertEquals(name.toLowerCase().trim(), themes.getFirst().name());
        assertEquals(1, themes.size());
    }

    @Test
    @Transactional
    void deveDetalharExistente() {
        String name = "Theme name";
        ThemeDto saved = themeService.save(new SaveThemeDto(name));

        ThemeDto detailed = themeService.detail(saved.id());

        assertEquals(name.toLowerCase().trim(), detailed.name());
    }

    @Test
    @Transactional
    void deveSalvarComOsDadosNecessarios() {
        String name = "Theme name";
        ThemeDto themeDto = themeService.save(new SaveThemeDto(name));

        assertEquals(name.toLowerCase().trim(), themeDto.name());
    }

    @Test
    @Transactional
    void deveAtualizarComOsDadosNecessarios() {
        ThemeDto savedDto = themeService.save(new SaveThemeDto("Theme name"));

        String name = " New Theme name";
        ThemeDto updatedDto = themeService.update(new UpdateThemeDto(savedDto.id(), name));

        assertEquals(name.toLowerCase().trim(), updatedDto.name());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String name = "Theme name";
        ThemeDto themeDto = themeService.save(new SaveThemeDto(name));

        assertDoesNotThrow(() -> themeService.delete(themeDto.id()));
    }

}