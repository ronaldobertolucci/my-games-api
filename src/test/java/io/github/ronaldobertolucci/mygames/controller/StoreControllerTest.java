package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.store.Store;
import io.github.ronaldobertolucci.mygames.model.store.StoreDto;
import io.github.ronaldobertolucci.mygames.model.store.SaveStoreDto;
import io.github.ronaldobertolucci.mygames.service.store.StoreService;
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

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreService storeService;

    @Test
    void deveListarTodasAsLojas() throws Exception {
        Page<Store> stores = new PageImpl<>(
                List.of(new Store(new SaveStoreDto("Store Name"))),
                PageRequest.of(0,20),
                1);
        when(storeService.findAll(any())).thenReturn(stores.map(StoreDto::new));

        mockMvc.perform(get("/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("store name"));
    }

    @Test
    void deveDetalharLoja() throws Exception {
        when(storeService.detail(1L)).thenReturn(new StoreDto(1L, "store name"));

        mockMvc.perform(get("/stores/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("store name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarLojaNoDetalhamento() throws Exception {
        when(storeService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/stores/{id}", 1L))
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

        mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoNomeValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Store name"
            }
            """;

        when(storeService.save(any())).thenReturn(new StoreDto(1L,"store name"));

        mockMvc.perform(post("/stores")
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

        mockMvc.perform(put("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Store Name"
            }
            """;

        mockMvc.perform(put("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarLoja() throws Exception {
        mockMvc.perform(delete("/stores/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarLojaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Store.class, 1L))
                .when(storeService).delete(1L);

        mockMvc.perform(delete("/stores/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}