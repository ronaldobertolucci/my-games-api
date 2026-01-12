package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.mygame.MyGame;
import io.github.ronaldobertolucci.mygames.model.mygame.MyGameDto;
import io.github.ronaldobertolucci.mygames.model.platform.Platform;
import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.mygame.MyGameService;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyGameController.class)
@Import({SecurityConfigurations.class})
class MyGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private MyGameService myGameService;

    @Test
    void deveProbirListarTodosOsMeusJogosParaNaoAutenticado() throws Exception {
        Page<MyGameDto> myGames = new PageImpl<>(
                List.of(new MyGameDto(getGenericMyGame())),
                PageRequest.of(0,20),
                1);
        when(myGameService.findByUser(any(), any())).thenReturn(myGames);

        mockMvc.perform(get("/my-games"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodosOsMeusJogosParaAutenticado() throws Exception {
        Page<MyGameDto> myGames = new PageImpl<>(
                List.of(new MyGameDto(getGenericMyGame())),
                PageRequest.of(0,20),
                1);
        when(myGameService.findByUser(any(), any())).thenReturn(myGames);

        mockMvc.perform(get("/my-games")
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].game.title").value("game title"))
                .andExpect(jsonPath("$.content[0].platform.name").value("platform name"))
                .andExpect(jsonPath("$.content[0].source.name").value("source name"));
    }

    @Test
    void deveProibirDetalharMeuJogoParaNaoAutenticado() throws Exception {
        when(myGameService.detail(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(get("/my-games/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDetalharMeuJogoParaAutenticado() throws Exception {
        when(myGameService.detail(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(get("/my-games/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.title").value("game title"))
                .andExpect(jsonPath("$.platform.name").value("platform name"))
                .andExpect(jsonPath("$.source.name").value("source name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarMeuJogoNoDetalhamento() throws Exception {
        when(myGameService.detail(any(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/my-games/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveFalharQuandoIdJogoNuloNaCriacao() throws Exception {
        String requestBody = """
            {
                "platform_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdPlataformaNuloNaCriacao() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdOrigemNuloNaCriacao() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "platform_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoStatusNaoExistirNaCriacao() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "STATUS_INEXISTENTE"
            }
            """;

        when(myGameService.save(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirCriarQuandoObjetoValidoNaCriacaoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        when(myGameService.save(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarQuandoObjetoValidoNaCriacaoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1
            }
            """;

        when(myGameService.save(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(post("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isCreated());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdJogoNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdPlataformaNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "source_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdOrigemNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "platform_id": 1,
                "status": "PLAYING"
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoStatusNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoStatusNaoExistirNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "STATUS_INEXISTENTE"
            }
            """;

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deveProibirAtualizarQuandoObjetoValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "ON_HOLD"
            }
            """;

        when(myGameService.update(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarQuandoObjetoValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "game_id": 1,
                "platform_id": 1,
                "source_id": 1,
                "status": "ON_HOLD"
            }
            """;

        when(myGameService.update(any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(put("/my-games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk());
    }


    @Test
    void deveProibirDeletarMeuJogoParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/my-games/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDeletarMeuJogoParaAutenticado() throws Exception {
        mockMvc.perform(delete("/my-games/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarMeuJogoNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(MyGame.class, 1L))
                .when(myGameService).delete(any(), any());

        mockMvc.perform(delete("/my-games/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveProibirMudarStatusMeuJogoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "status": "ON_HOLD"
            }
            """;

        when(myGameService.updateStatus(any(), any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(patch("/my-games/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveFalharMudarStatusMeuJogoQuandoNaoExistirStatusParaAutenticado() throws Exception {
        String requestBody = """
            {
                "status": "STATUS_INEXISTENTE"
            }
            """;

        when(myGameService.updateStatus(any(), any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(patch("/my-games/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveMudarStatusMeuJogoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "status": "ON_HOLD"
            }
            """;

        when(myGameService.updateStatus(any(), any(), any())).thenReturn(new MyGameDto(getGenericMyGame()));

        mockMvc.perform(patch("/my-games/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk());
    }

    private MyGame getGenericMyGame() {
        MyGame myGame = new MyGame();
        myGame.setUser(getUser(1L, "Username"));
        myGame.setGame(getGame(1L, "Game title", getCompany(1L, "Company name")));
        myGame.setPlatform(getPlatform(1L, "Platform name"));
        myGame.setSource(getSource(1L, "Source name"));
        return myGame;
    }

    private User getUser(Long id, String username) {
        User user =  new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Platform getPlatform(Long id, String name) {
        Platform platform = new Platform();
        platform.setId(id);
        platform.setName(name);
        return platform;
    }

    private Source getSource(Long id, String name) {
        Source source = new Source();
        source.setId(id);
        source.setName(name);
        return source;
    }

    private Company getCompany(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private Game getGame(Long id, String title, Company company) {
        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setCompany(company);
        return game;
    }
}