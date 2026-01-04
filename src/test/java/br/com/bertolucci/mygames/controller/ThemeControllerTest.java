package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.theme.Theme;
import br.com.bertolucci.mygames.model.theme.ThemeDto;
import br.com.bertolucci.mygames.model.theme.SaveThemeDto;
import br.com.bertolucci.mygames.service.theme.ThemeService;
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

@WebMvcTest(ThemeController.class)
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThemeService themeService;

    @Test
    void deveListarTodasOsTemas() throws Exception {
        Page<Theme> themes = new PageImpl<>(
                List.of(new Theme(new SaveThemeDto("Theme Name"))),
                PageRequest.of(0,20),
                1);
        when(themeService.findAll(any())).thenReturn(themes.map(ThemeDto::new));

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("theme name"));
    }

    @Test
    void deveDetalharTema() throws Exception {
        when(themeService.detail(1L)).thenReturn(new ThemeDto(1L, "theme name"));

        mockMvc.perform(get("/themes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("theme name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarTemaNoDetalhamento() throws Exception {
        when(themeService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/themes/{id}", 1L))
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
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoNomeValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Theme name"
            }
            """;

        when(themeService.save(any())).thenReturn(new ThemeDto(1L,"theme name"));

        mockMvc.perform(post("/themes")
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

        mockMvc.perform(put("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarTema() throws Exception {
        mockMvc.perform(delete("/themes/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarTemaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Theme.class, 1L))
                .when(themeService).delete(1L);

        mockMvc.perform(delete("/themes/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}