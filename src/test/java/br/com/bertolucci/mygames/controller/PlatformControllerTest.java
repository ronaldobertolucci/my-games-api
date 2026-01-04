package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.platform.Platform;
import br.com.bertolucci.mygames.model.platform.PlatformDto;
import br.com.bertolucci.mygames.model.platform.SavePlatformDto;
import br.com.bertolucci.mygames.model.store.Store;
import br.com.bertolucci.mygames.model.store.StoreDto;
import br.com.bertolucci.mygames.service.platform.PlatformService;
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

@WebMvcTest(PlatformController.class)
class PlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlatformService platformService;

    @Test
    void deveListarTodasAsCompanhias() throws Exception {
        var store = new Store();
        store.setId(1L);
        store.setName("Store Name");
        var platform = new Platform(new SavePlatformDto("Platform Name", 1L));
        platform.setStore(store);

        Page<Platform> platforms = new PageImpl<>(
                List.of(platform),
                PageRequest.of(0,20),
                1);
        when(platformService.findAll(any())).thenReturn(platforms.map(PlatformDto::new));

        mockMvc.perform(get("/platforms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("platform name"))
                .andExpect(jsonPath("$.content[0].store.id").value(1L))
                .andExpect(jsonPath("$.content[0].store.name").value("store name"));
    }

    @Test
    void deveDetalharCompanhia() throws Exception {
        when(platformService.detail(1L)).thenReturn(new PlatformDto(1L, "platform name", new StoreDto(1L, "store name")));

        mockMvc.perform(get("/platforms/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("platform name"))
                .andExpect(jsonPath("$.store.id").value(1L))
                .andExpect(jsonPath("$.store.name").value("store name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNoDetalhamento() throws Exception {
        when(platformService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/platforms/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveFalharQuandoNomeInvalidoNaCriacao(String name) throws Exception {
        String requestBody = """
            {
                "name": "%s",
                "store_id": 1
            }
            """.formatted(name);

        mockMvc.perform(post("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdStoreNuloNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Platform name"
            }
            """;

        mockMvc.perform(post("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Platform name",
                "store_id": 1
            }
            """;

        when(platformService.save(any())).thenReturn(new PlatformDto(1L,"platform name", new StoreDto(1L, "store name")));

        mockMvc.perform(post("/platforms")
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
                "name": "%s",
                "store_id": 1
            }
            """.formatted(name);

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdStoreNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "%s"
            }
            """;

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Platform Name",
                "store_id": 1
            }
            """;

        mockMvc.perform(put("/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarCompanhia() throws Exception {
        mockMvc.perform(delete("/platforms/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Platform.class, 1L))
                .when(platformService).delete(1L);

        mockMvc.perform(delete("/platforms/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}