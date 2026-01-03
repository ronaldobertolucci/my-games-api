package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.company.Company;
import br.com.bertolucci.mygames.model.company.CompanyDto;
import br.com.bertolucci.mygames.model.company.SaveCompanyDto;
import br.com.bertolucci.mygames.service.company.CompanyService;
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

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @Test
    void deveListarTodasAsCompanhias() throws Exception {
        Page<Company> companies = new PageImpl<>(
                List.of(new Company(new SaveCompanyDto("Company Name"))),
                PageRequest.of(0,20),
                1);
        when(companyService.findAll(any())).thenReturn(companies.map(CompanyDto::new));

        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Company Name"));
    }

    @Test
    void deveDetalharCompanhia() throws Exception {
        when(companyService.detail(1L)).thenReturn(new CompanyDto(1L, "Company Name"));

        mockMvc.perform(get("/companies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Company Name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNoDetalhamento() throws Exception {
        when(companyService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/companies/{id}", 1L))
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

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarQuandoNomeValidoNaCriacao() throws Exception {
        String requestBody = """
            {
                "name": "Company name"
            }
            """;

        when(companyService.save(any())).thenReturn(new CompanyDto(1L,"Company name"));

        mockMvc.perform(post("/companies")
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

        mockMvc.perform(put("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFalharQuandoIdNuloNaAtualizacao() throws Exception {
        String requestBody = """
            {
                "name": "Company Name"
            }
            """;

        mockMvc.perform(put("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarCompanhia() throws Exception {
        mockMvc.perform(delete("/companies/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Company.class, 1L))
                .when(companyService).delete(1L);

        mockMvc.perform(delete("/companies/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}