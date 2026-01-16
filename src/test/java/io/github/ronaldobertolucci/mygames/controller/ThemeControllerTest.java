package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.theme.Theme;
import io.github.ronaldobertolucci.mygames.model.theme.ThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.SaveThemeDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.theme.ThemeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(ThemeController.class)
@Import({SecurityConfigurations.class})
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ThemeService themeService;

    @Test
    void deveProibirListarTodosOsTemasParaNaoAutenticado() throws Exception {
        List<Theme> themes = List.of(new Theme(new SaveThemeDto("Theme Name")));
        when(themeService.findAll(any())).thenReturn(new PageImpl<>(themes.stream().map(ThemeDto::new).toList(),
                PageRequest.of(0,20), themes.size()));

        mockMvc.perform(get("/themes"))
                .andExpect(status().isForbidden());
    }


    @Test
    void deveListarTodosOsTemasParaAutenticado() throws Exception {
        List<Theme> themes = List.of(new Theme(new SaveThemeDto("Theme Name")));
        when(themeService.findAll(any())).thenReturn(new PageImpl<>(themes.stream().map(ThemeDto::new).toList(),
                PageRequest.of(0,20), themes.size()));

        mockMvc.perform(get("/themes")
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("theme name"));
    }

    @Test
    void deveProibirDetalharTemaParaNaoAutenticado() throws Exception {
        when(themeService.detail(1L)).thenReturn(new ThemeDto(1L, "theme name"));

        mockMvc.perform(get("/themes/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDetalharTemaParaAutenticado() throws Exception {
        when(themeService.detail(1L)).thenReturn(new ThemeDto(1L, "theme name"));

        mockMvc.perform(get("/themes/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("theme name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarTemaNoDetalhamento() throws Exception {
        when(themeService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/themes/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
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

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirCriarQuandoNomeValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Theme name"
            }
            """;

        when(themeService.save(any())).thenReturn(new ThemeDto(1L,"theme name"));

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarQuandoNomeValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Theme name"
            }
            """;

        when(themeService.save(any())).thenReturn(new ThemeDto(1L,"theme name"));

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isCreated());
    }

    @Test
    void deveProibirAtualizarQuandoNomeValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Theme name"
            }
            """;

        when(themeService.save(any())).thenReturn(new ThemeDto(1L,"theme name"));

        mockMvc.perform(put("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarQuandoNomeValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Theme name"
            }
            """;

        when(themeService.save(any())).thenReturn(new ThemeDto(1L,"theme name"));

        mockMvc.perform(put("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk());
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

        mockMvc.perform(put("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Theme Name"
            }
            """;

        mockMvc.perform(put("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirDeletarTemaParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/themes/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDeletarTemaParaAutenticado() throws Exception {
        mockMvc.perform(delete("/themes/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarTemaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Theme.class, 1L))
                .when(themeService).delete(1L);

        mockMvc.perform(delete("/themes/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }
}