package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.company.SaveCompanyDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.company.CompanyService;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
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

@WebMvcTest(CompanyController.class)
@Import({SecurityConfigurations.class})
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CompanyService companyService;

    @Test
    void deveProbirListarTodasAsCompanhiasParaNaoAutenticado() throws Exception {
        List<Company> companies = List.of(new Company(new SaveCompanyDto("Company Name")));
        when(companyService.findAll()).thenReturn(companies.stream().map(CompanyDto::new).toList());

        mockMvc.perform(get("/companies"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodasAsCompanhiasParaAutenticado() throws Exception {
        List<Company> companies = List.of(new Company(new SaveCompanyDto("Company Name")));
        when(companyService.findAll()).thenReturn(companies.stream().map(CompanyDto::new).toList());

        mockMvc.perform(get("/companies")
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("company name"));
    }

    @Test
    void deveProibirDetalharCompanhiaParaNaoAutenticado() throws Exception {
        when(companyService.detail(1L)).thenReturn(new CompanyDto(1L, "company name"));

        mockMvc.perform(get("/companies/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDetalharCompanhiaParaAutenticado() throws Exception {
        when(companyService.detail(1L)).thenReturn(new CompanyDto(1L, "company name"));

        mockMvc.perform(get("/companies/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("company name"));
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNoDetalhamento() throws Exception {
        when(companyService.detail(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/companies/{id}", 1L)
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

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirCriarQuandoNomeValidoNaCriacaoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Company name"
            }
            """;

        when(companyService.save(any())).thenReturn(new CompanyDto(1L,"company name"));

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarQuandoNomeValidoNaCriacaoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "name": "Company name"
            }
            """;

        when(companyService.save(any())).thenReturn(new CompanyDto(1L,"company name"));

        mockMvc.perform(post("/companies")
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

        mockMvc.perform(put("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirAtualizarQuandoNomeValidoParaNaoAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Company name"
            }
            """;

        when(companyService.update(any())).thenReturn(new CompanyDto(1L,"company name"));

        mockMvc.perform(put("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAtualizarQuandoNomeValidoParaAutenticado() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "name": "Company name"
            }
            """;

        when(companyService.update(any())).thenReturn(new CompanyDto(1L,"company name"));

        mockMvc.perform(put("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isOk());
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
                        .content(requestBody)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveProibirDeletarCompanhiaParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/companies/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDeletarCompanhiaParaAutenticado() throws Exception {
        mockMvc.perform(delete("/companies/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharQuandoNaoEncontrarCompanhiaNaDelecao() throws Exception {
        doThrow(new ObjectRetrievalFailureException(Company.class, 1L))
                .when(companyService).delete(1L);

        mockMvc.perform(delete("/companies/{id}", 1L)
                        .with(user("test").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }
}