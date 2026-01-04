package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.genre.Genre;
import br.com.bertolucci.mygames.model.genre.GenreDto;
import br.com.bertolucci.mygames.model.genre.SaveGenreDto;
import br.com.bertolucci.mygames.service.genre.GenreService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void deveListarTodasOsGeneros() throws Exception {
        Page<Genre> genres = new PageImpl<>(
                List.of(new Genre(new SaveGenreDto("Genre Name"))),
                PageRequest.of(0,20),
                1);
        when(genreService.findAll(any())).thenReturn(genres.map(GenreDto::new));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("genre name"));
    }

    @Test
    void deveDetalharGenero() throws Exception {
        when(genreService.detail(1L)).thenReturn(new GenreDto(1L, "genre name"));

        mockMvc.perform(get("/genres/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("genre name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarGeneroNoDetalhamento() throws Exception {
        when(genreService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/genres/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveFalharQuandoNomeInvalidoNaCriacao(String name) throws Exception {
        String requestBody = """
            {
                "name": "%s"
            }
            """.formatted(name);

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoNomeValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Genre name"
            }
            """;

        when(genreService.save(any())).thenReturn(new GenreDto(1L,"genre name"));

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveFalharQuandoNomeInvalidoNaAtualizacao(String name) throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "%s"
            }
            """.formatted(name);

        mockMvc.perform(put("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Genre Name"
            }
            """;

        mockMvc.perform(put("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarGenero() throws Exception {
        mockMvc.perform(delete("/genres/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarGeneroNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Genre.class, 1L))
                .when(genreService).delete(1L);

        mockMvc.perform(delete("/genres/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}