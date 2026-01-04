package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.genre.Genre;
import io.github.ronaldobertolucci.mygames.service.game.GameService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    void deveListarTodasAsJogos() throws Exception {
        Company company = new Company();
        company.setId(1L);
        company.setName("Company name");

        Game game = new Game();
        game.setId(1L);
        game.setTitle("Game title");
        game.setDescription("Game description");
        game.setReleasedAt(LocalDate.parse("2026-02-01"));
        game.setCompany(company);
        game.setGenres(new HashSet<>());

        Page<Game> games = new PageImpl<>(
                List.of(game),
                PageRequest.of(0,20),
                1);
        when(gameService.findAll(any())).thenReturn(games.map(GameDto::new));

        mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("game title"))
                .andExpect(jsonPath("$.content[0].description").value("game description"))
                .andExpect(jsonPath("$.content[0].released_at").value("2026-02-01"))
                .andExpect(jsonPath("$.content[0].company.id").value(1L))
                .andExpect(jsonPath("$.content[0].company.name").value("company name"));
    }

    @Test
    void deveDetalharJogo() throws Exception {
        when(gameService.detail(1L)).thenReturn(new GameDto(1L, "game title", "game description", LocalDate.parse("2026-02-01"), new CompanyDto(1L, "company name"), new ArrayList<>()));

        mockMvc.perform(get("/games/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("game title"))
                .andExpect(jsonPath("$.description").value("game description"))
                .andExpect(jsonPath("$.released_at").value("2026-02-01"))
                .andExpect(jsonPath("$.company.id").value(1L))
                .andExpect(jsonPath("$.company.name").value("company name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarJogoNoDetalhamento() throws Exception {
        when(gameService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/games/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveFalharQuandoTituloInvalidoNaCriacao(String title) throws Exception {
        String requestBody = """
            {
                "title": "%s",
                "company_id": 1L
            }
            """.formatted(title);

        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdCompanhiaNuloNaCriacao() throws Exception {
        String requestBody = """
            {
                "title": "Game title"
            }
            """;

        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "title": "game title",
                "description": "game description",
                "released_at": "2026-02-01",
                "company_id": 1,
                "genre_ids": []
            }
            """;

        when(gameService.save(any())).thenReturn(new GameDto(1L, "game title", "game description", LocalDate.parse("2026-02-01"), new CompanyDto(1L, "company name"), new ArrayList<>()));

        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveFalharQuandoTituloInvalidoNaAtualizacao(String title) throws Exception {
        String requestBody = """
            {
                "id": 1,
                "title": "%s",
                "company_id": 1L
            }
            """.formatted(title);

        mockMvc.perform(put("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "title": "Game title",
                "company_id": 1L
            }
            """;

        mockMvc.perform(put("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdCompanhiaNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1L,
                "title": "Game title",
            }
            """;

        mockMvc.perform(put("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarJogo() throws Exception {
        mockMvc.perform(delete("/games/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarJogoNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Game.class, 1L))
                .when(gameService).delete(1L);

        mockMvc.perform(delete("/games/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveFalharQuandoNaoEncontrarGeneroNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Genre.class, 1L))
                .when(gameService).removeGenre(1L, 1L);

        mockMvc.perform(delete("/games/{id}/genres/{genreId}", 1L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveFalharQuandoNaoEncontrarGeneroNaAdicao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Genre.class, 1L))
                .when(gameService).addGenre(1L, 1L);

        mockMvc.perform(post("/games/{id}/genres/{genreId}", 1L, 1L))
                .andExpect(status().isNotFound());
    }
}