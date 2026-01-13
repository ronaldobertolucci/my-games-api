package io.github.ronaldobertolucci.mygames.service.game;

import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyRepository;
import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.game.SaveGameDto;
import io.github.ronaldobertolucci.mygames.model.game.UpdateGameDto;
import io.github.ronaldobertolucci.mygames.model.genre.Genre;
import io.github.ronaldobertolucci.mygames.model.genre.GenreRepository;
import io.github.ronaldobertolucci.mygames.model.theme.Theme;
import io.github.ronaldobertolucci.mygames.model.theme.ThemeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GameServiceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private GameService gameService;

    @Test
    @Transactional
    void deveListarJogos() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        String gameDescription = "RPG massa";
        LocalDate gameReleasedAt = LocalDate.parse("2015-01-01");
        SaveGameDto dto = new SaveGameDto(gameTitle, gameDescription, gameReleasedAt, company.getId(), List.of(genre.getId()), List.of(theme.getId()));
        gameService.save(dto);

        List<GameDto> games = gameService.findAll();

        assertEquals(gameTitle.toLowerCase().trim(), games.getFirst().title());
        assertEquals(gameDescription.toLowerCase().trim(), games.getFirst().description());
        assertEquals(gameReleasedAt, games.getFirst().releasedAt());
        assertEquals(company.getId(), games.getFirst().company().id());
        assertEquals(1, games.getFirst().genres().size());
        assertEquals("rpg", games.getFirst().genres().getFirst().name());
        assertEquals(1, games.getFirst().themes().size());
        assertEquals("open world", games.getFirst().themes().getFirst().name());
        assertEquals(1, games.size());
    }

    @Test
    @Transactional
    void deveDetalharJogoComTodosOsDados() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        String gameDescription = "RPG massa";
        LocalDate gameReleasedAt = LocalDate.parse("2015-01-01");
        SaveGameDto dto = new SaveGameDto(gameTitle, gameDescription, gameReleasedAt, company.getId(), List.of(genre.getId()), List.of(theme.getId()));
        GameDto game = gameService.save(dto);

        GameDto loadedGame = gameService.detail(game.id());

        assertEquals(gameTitle.toLowerCase().trim(), loadedGame.title());
        assertEquals(gameDescription.toLowerCase().trim(), loadedGame.description());
        assertEquals(gameReleasedAt, loadedGame.releasedAt());
        assertEquals(company.getId(), loadedGame.company().id());
        assertEquals(1, loadedGame.genres().size());
        assertEquals("rpg", loadedGame.genres().getFirst().name());
        assertEquals(1, loadedGame.themes().size());
        assertEquals("open world", loadedGame.themes().getFirst().name());
    }

    @Test
    @Transactional
    void deveDetalharJogoComDadosNecessarios() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        SaveGameDto dto = new SaveGameDto(gameTitle, null, null, company.getId(), null, null);
        GameDto game = gameService.save(dto);

        GameDto loadedGame = gameService.detail(game.id());

        assertEquals(gameTitle.toLowerCase().trim(), loadedGame.title());
        assertEquals(company.getId(), loadedGame.company().id());
        assertNull(loadedGame.description());
        assertNull(loadedGame.releasedAt());
        assertTrue(loadedGame.genres().isEmpty());
        assertTrue(loadedGame.themes().isEmpty());
    }

    @Test
    @Transactional
    void deveSalvarJogoComTodosOsDados() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        String gameDescription = "RPG massa";
        LocalDate gameReleasedAt = LocalDate.parse("2015-01-01");
        SaveGameDto dto = new SaveGameDto(gameTitle, gameDescription, gameReleasedAt, company.getId(), List.of(genre.getId()), List.of(theme.getId()));
        GameDto game = gameService.save(dto);

        assertEquals(gameTitle.toLowerCase().trim(), game.title());
        assertEquals(gameDescription.toLowerCase().trim(), game.description());
        assertEquals(gameReleasedAt, game.releasedAt());
        assertEquals(company.getId(), game.company().id());
        assertEquals(1, game.genres().size());
        assertEquals("rpg", game.genres().getFirst().name());
        assertEquals(1, game.themes().size());
        assertEquals("open world", game.themes().getFirst().name());
    }

    @Test
    @Transactional
    void deveSalvarJogoComDadosNecessarios() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        SaveGameDto dto = new SaveGameDto(gameTitle, null, null, company.getId(), null, null);
        GameDto game = gameService.save(dto);

        assertEquals(gameTitle.toLowerCase().trim(), game.title());
        assertEquals(company.getId(), game.company().id());
        assertNull(game.description());
        assertNull(game.releasedAt());
        assertTrue(game.genres().isEmpty());
        assertTrue(game.themes().isEmpty());
    }

    @Test
    @Transactional
    void deveAtualizarJogoComTodosOsDados() {
        Genre genre1 = genreRepository.save(getGenre(null,"RPG"));
        Genre genre2 = genreRepository.save(getGenre(null,"FPS"));
        Theme theme1 = themeRepository.save(getTheme(null,"open world "));
        Theme theme2 = themeRepository.save(getTheme(null," war"));
        Company company1 = companyRepository.save(getCompany(null, "CD Projekt"));
        Company company2 = companyRepository.save(getCompany(null, "Kojima"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company1.getId(), List.of(genre1.getId()), List.of(theme1.getId()));
        GameDto saved = gameService.save(savedDto);

        String gameTitle = "CS Go";
        String gameDescription = "FPS massa";
        LocalDate gameReleasedAt = LocalDate.parse("2018-01-01");
        UpdateGameDto updatedDto = new UpdateGameDto(saved.id(), gameTitle, gameDescription, gameReleasedAt, company2.getId(), List.of(genre2.getId()), List.of(theme2.getId()));
        GameDto updated = gameService.update(updatedDto);

        assertEquals(gameTitle.toLowerCase().trim(), updated.title());
        assertEquals(gameDescription.toLowerCase().trim(), updated.description());
        assertEquals(gameReleasedAt, updated.releasedAt());
        assertEquals(company2.getId(), updated.company().id());
        assertEquals(1, updated.genres().size());
        assertEquals("fps", updated.genres().getFirst().name());
        assertEquals(1, updated.themes().size());
        assertEquals("war", updated.themes().getFirst().name());
    }

    @Test
    @Transactional
    void deveAtualizarJogoComDadosNecessarios() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company1 = companyRepository.save(getCompany(null, "CD Projekt"));
        Company company2 = companyRepository.save(getCompany(null, "Kojima"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company1.getId(), List.of(genre.getId()), List.of(theme.getId()));
        GameDto saved = gameService.save(savedDto);

        String gameTitle = "CS Go";
        UpdateGameDto updatedDto = new UpdateGameDto(saved.id(), gameTitle, null, null, company2.getId(), null, null);
        GameDto updated = gameService.update(updatedDto);

        assertEquals(gameTitle.toLowerCase().trim(), updated.title());
        assertEquals(company2.getId(), updated.company().id());
        assertNull(updated.description());
        assertNull(updated.releasedAt());
        assertTrue(updated.genres().isEmpty());
        assertTrue(updated.themes().isEmpty());
    }

    @Test
    @Transactional
    void deveDeletarJogoSalvo() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Theme theme = themeRepository.save(getTheme(null,"open world "));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        String gameTitle = "The Witcher";
        String gameDescription = "RPG massa";
        LocalDate gameReleasedAt = LocalDate.parse("2015-01-01");
        SaveGameDto dto = new SaveGameDto(gameTitle, gameDescription, gameReleasedAt, company.getId(), List.of(genre.getId()), List.of(theme.getId()));
        GameDto game = gameService.save(dto);

        assertDoesNotThrow(() -> gameService.delete(game.id()));
    }

    @Test
    @Transactional
    void deveAdicionarGeneroAoJogo() {
        Genre genre1 = genreRepository.save(getGenre(null,"RPG"));
        Genre genre2 = genreRepository.save(getGenre(null,"FPS"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company.getId(), List.of(genre1.getId()), null);
        GameDto game = gameService.save(savedDto);
        assertEquals(1, game.genres().size());

        GameDto added = gameService.addGenre(game.id(), genre2.getId());
        assertEquals(2, added.genres().size());
    }

    @Test
    @Transactional
    void deveRemoverGeneroAoJogo() {
        Genre genre = genreRepository.save(getGenre(null,"RPG"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company.getId(), List.of(genre.getId()), null);
        GameDto game = gameService.save(savedDto);
        assertEquals(1, game.genres().size());

        GameDto removed = gameService.removeGenre(game.id(), genre.getId());
        assertTrue(removed.genres().isEmpty());
    }

    @Test
    @Transactional
    void deveAdicionarTemaAoJogo() {
        Theme theme1 = themeRepository.save(getTheme(null,"RPG"));
        Theme theme2 = themeRepository.save(getTheme(null,"FPS"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company.getId(), null, List.of(theme1.getId()));
        GameDto game = gameService.save(savedDto);
        assertEquals(1, game.themes().size());

        GameDto added = gameService.addTheme(game.id(), theme2.getId());
        assertEquals(2, added.themes().size());
    }

    @Test
    @Transactional
    void deveRemoverTemaAoJogo() {
        Theme theme = themeRepository.save(getTheme(null,"RPG"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));

        SaveGameDto savedDto = new SaveGameDto("The Witcher", "RPG massa", LocalDate.parse("2015-01-01"), company.getId(), null, List.of(theme.getId()));
        GameDto game = gameService.save(savedDto);
        assertEquals(1, game.themes().size());

        GameDto removed = gameService.removeTheme(game.id(), theme.getId());
        assertTrue(removed.themes().isEmpty());
    }

    private Company getCompany(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private Genre getGenre(Long id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);
        return genre;
    }

    private Theme getTheme(Long id, String name) {
        Theme theme = new Theme();
        theme.setId(id);
        theme.setName(name);
        return theme;
    }

    private Game getGame(Long id, String title, Company company, List<Genre> genres, List<Theme> themes) {
        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setCompany(company);
        game.getGenres().addAll(genres);
        game.getThemes().addAll(themes);
        return game;
    }

}