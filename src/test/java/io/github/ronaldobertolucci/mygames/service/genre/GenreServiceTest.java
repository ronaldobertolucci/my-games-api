package io.github.ronaldobertolucci.mygames.service.genre;

import io.github.ronaldobertolucci.mygames.model.genre.GenreDto;
import io.github.ronaldobertolucci.mygames.model.genre.SaveGenreDto;
import io.github.ronaldobertolucci.mygames.model.genre.UpdateGenreDto;
import io.github.ronaldobertolucci.mygames.service.genre.GenreService;
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
class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @Test
    @Transactional
    void deveListarVazioSeNomeNaoEncontrado() {
        String name = "Genre name";
        GenreDto genreDto = genreService.save(new SaveGenreDto(name));

        List<GenreDto> genres = genreService.findByNameContaining("banana");

        assertEquals(0, genres.size());
    }

    @Test
    @Transactional
    void deveListarPeloNome() {
        String name = "Genre name";
        GenreDto genreDto = genreService.save(new SaveGenreDto(name));

        List<GenreDto> genres = genreService.findByNameContaining("name");

        assertEquals(name.toLowerCase().trim(), genres.getFirst().name());
        assertEquals(1, genres.size());
    }
    
    @Test
    @Transactional
    void deveListarTodos() {
        String name = "Genre name";
        GenreDto genreDto = genreService.save(new SaveGenreDto(name));

        List<GenreDto> genres = genreService.findAll();

        assertEquals(name.toLowerCase().trim(), genres.getFirst().name());
        assertEquals(1, genres.size());
    }

    @Test
    @Transactional
    void deveDetalharExistente() {
        String name = "Genre name";
        GenreDto saved = genreService.save(new SaveGenreDto(name));

        GenreDto detailed = genreService.detail(saved.id());

        assertEquals(name.toLowerCase().trim(), detailed.name());
    }

    @Test
    @Transactional
    void deveSalvarComOsDadosNecessarios() {
        String name = "Genre name";
        GenreDto genreDto = genreService.save(new SaveGenreDto(name));

        assertEquals(name.toLowerCase().trim(), genreDto.name());
    }

    @Test
    @Transactional
    void deveAtualizarComOsDadosNecessarios() {
        GenreDto savedDto = genreService.save(new SaveGenreDto("Genre name"));

        String name = " New Genre name";
        GenreDto updatedDto = genreService.update(new UpdateGenreDto(savedDto.id(), name));

        assertEquals(name.toLowerCase().trim(), updatedDto.name());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String name = "Genre name";
        GenreDto genreDto = genreService.save(new SaveGenreDto(name));

        assertDoesNotThrow(() -> genreService.delete(genreDto.id()));
    }

}