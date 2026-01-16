package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.platform.Platform;
import io.github.ronaldobertolucci.mygames.model.platform.PlatformDto;
import io.github.ronaldobertolucci.mygames.model.platform.SavePlatformDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.platform.PlatformService;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
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

@WebMvcTest(PlatformController.class)
@Import({SecurityConfigurations.class})
class PlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PlatformService platformService;

    @Test
    void deveProibirListarTodasAsPlataformasParaNaoAutenticado() throws Exception {
        List<Platform> platforms = List.of(new Platform(new SavePlatformDto("Platform Name")));
        when(platformService.findAll(any())).thenReturn(new PageImpl<>(platforms.stream().map(PlatformDto::new).toList(),
                PageRequest.of(0, 20), platforms.size()));

        mockMvc.perform(get("/platforms"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodasAsPlataformasParaAutenticado() throws Exception {
        List<Platform> platforms = List.of(new Platform(new SavePlatformDto("Platform Name")));
        when(platformService.findAll(any())).thenReturn(new PageImpl<>(platforms.stream().map(PlatformDto::new).toList(),
                PageRequest.of(0, 20), platforms.size()));

        mockMvc.perform(get("/platforms")
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("platform name"));
    }

    @Test
    void deveDetalharPlataformaParaNaoAutenticado() throws Exception {
        when(platformService.detail(1L)).thenReturn(new PlatformDto(1L, "platform name" ));

        mockMvc.perform(get("/platforms/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDetalharPlataformaParaAutenticado() throws Exception {
        when(platformService.detail(1L)).thenReturn(new PlatformDto(1L, "platform name" ));

        mockMvc.perform(get("/platforms/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("platform name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarPlataformaNoDetalhamento() throws Exception {
        when(platformService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/platforms/{id}", 1L)
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

        mockMvc.perform(post("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirCriarQuandoValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Platform name"
            }
            """;

        when(platformService.save(any())).thenReturn(new PlatformDto(1L,"platform name"));

        mockMvc.perform(post("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarQuandoValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Platform name"
            }
            """;

        when(platformService.save(any())).thenReturn(new PlatformDto(1L,"platform name"));

        mockMvc.perform(post("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isCreated());
    }

    @Test
    void deveProibirAtualizarQuandoValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Platform name"
            }
            """;

        when(platformService.save(any())).thenReturn(new PlatformDto(1L,"platform name"));

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarQuandoValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Platform name"
            }
            """;

        when(platformService.save(any())).thenReturn(new PlatformDto(1L,"platform name"));

        mockMvc.perform(put("/platforms")
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

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Platform Name"
            }
            """;

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirDeletarPlataformaParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/platforms/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDeletarPlataformaParaAutenticado() throws Exception {
        mockMvc.perform(delete("/platforms/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarPlataformaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Platform.class, 1L))
                .when(platformService).delete(1L);

        mockMvc.perform(delete("/platforms/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }
}