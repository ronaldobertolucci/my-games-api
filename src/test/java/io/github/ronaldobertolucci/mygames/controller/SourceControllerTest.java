package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.source.SourceDto;
import io.github.ronaldobertolucci.mygames.model.source.SaveSourceDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.source.SourceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

@WebMvcTest(SourceController.class)
@Import({SecurityConfigurations.class})
class SourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private SourceService sourceService;

    @Test
    void deveProibirListarTodasAsLojasParaNaoAutenticado() throws Exception {
        List<Source> sources = List.of(new Source(new SaveSourceDto("Source Name")));
        when(sourceService.findAll()).thenReturn(sources.stream().map(SourceDto::new).toList());

        mockMvc.perform(get("/sources"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodasAsLojasParaAutenticado() throws Exception {
        List<Source> sources = List.of(new Source(new SaveSourceDto("Source Name")));
        when(sourceService.findAll()).thenReturn(sources.stream().map(SourceDto::new).toList());

        mockMvc.perform(get("/sources")
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("source name"));
    }

    @Test
    void deveProibirDetalharLojaParaNaoAutenticado() throws Exception {
        when(sourceService.detail(1L)).thenReturn(new SourceDto(1L, "source name"));

        mockMvc.perform(get("/sources/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDetalharLojaParaAutenticado() throws Exception {
        when(sourceService.detail(1L)).thenReturn(new SourceDto(1L, "source name"));

        mockMvc.perform(get("/sources/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("source name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarLojaNoDetalhamento() throws Exception {
        when(sourceService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/sources/{id}", 1L)
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

        mockMvc.perform(post("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirCriarQuandoNomeValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Source name"
            }
            """;

        when(sourceService.save(any())).thenReturn(new SourceDto(1L,"source name"));

        mockMvc.perform(post("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarQuandoNomeValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Source name"
            }
            """;

        when(sourceService.save(any())).thenReturn(new SourceDto(1L,"source name"));

        mockMvc.perform(post("/sources")
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
                "name": "Source name"
            }
            """;

        when(sourceService.save(any())).thenReturn(new SourceDto(1L,"source name"));

        mockMvc.perform(post("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarQuandoNomeValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Source name"
            }
            """;

        when(sourceService.save(any())).thenReturn(new SourceDto(1L,"source name"));

        mockMvc.perform(post("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
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

        mockMvc.perform(put("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Source Name"
            }
            """;

        mockMvc.perform(put("/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirDeletarLojaParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/sources/{id}", 1L))
                .andExpect(status().isForbidden());
    }


    @Test
    void deveDeletarLojaParaAutenticado() throws Exception {
        mockMvc.perform(delete("/sources/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarLojaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Source.class, 1L))
                .when(sourceService).delete(1L);

        mockMvc.perform(delete("/sources/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }
}