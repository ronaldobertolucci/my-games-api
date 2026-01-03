package br.com.bertolucci.mygames.model.genre;

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
class GenreTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private GenreRepository repository;

    @Test
    void deveSalvarCompanhiaComNome() {
        // given
        var genre = new Genre();
        genre.setName("Genre name ");
        em.persist(genre);

        //then
        Genre loadedGenre = repository.getReferenceById(genre.getId());
        assertEquals("genre name", loadedGenre.getName());
    }

    @Test
    void deveAtualizarCompanhiaComNome() {
        // given
        var genre = new Genre();
        genre.setName("Genre name ");
        em.persist(genre);

        Genre loadedGenre = repository.getReferenceById(genre.getId());

        //then
        loadedGenre.update(new UpdateGenreDto(loadedGenre.getId(), "New name"));
        assertEquals("new name", loadedGenre.getName());
    }
}
