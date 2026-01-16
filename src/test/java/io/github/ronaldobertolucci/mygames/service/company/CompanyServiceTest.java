package io.github.ronaldobertolucci.mygames.service.company;

import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.company.SaveCompanyDto;
import io.github.ronaldobertolucci.mygames.model.company.UpdateCompanyDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;
    
    @Test
    @Transactional
    void deveListarVazioSeNomeNaoEncontrado() {
        String name = "Company name";
        CompanyDto companyDto = companyService.save(new SaveCompanyDto(name));

        List<CompanyDto> companies = companyService.findByNameContaining("banana");

        assertEquals(0, companies.size());
    }

    @Test
    @Transactional
    void deveListarPeloNome() {
        String name = "Company name";
        CompanyDto companyDto = companyService.save(new SaveCompanyDto(name));

        List<CompanyDto> companies = companyService.findByNameContaining("name");

        assertEquals(name.toLowerCase().trim(), companies.getFirst().name());
        assertEquals(1, companies.size());
    }

    @Test
    @Transactional
    void deveListarTodos() {
        String name = "Company name";
        CompanyDto companyDto = companyService.save(new SaveCompanyDto(name));

        List<CompanyDto> companies = companyService.findAll();

        assertEquals(name.toLowerCase().trim(), companies.getFirst().name());
        assertEquals(1, companies.size());
    }

    @Test
    @Transactional
    void deveDetalharExistente() {
        String name = "Company name";
        CompanyDto saved = companyService.save(new SaveCompanyDto(name));

        CompanyDto detailed = companyService.detail(saved.id());

        assertEquals(name.toLowerCase().trim(), detailed.name());
    }

    @Test
    @Transactional
    void deveSalvarComOsDadosNecessarios() {
        String name = "Company name";
        CompanyDto companyDto = companyService.save(new SaveCompanyDto(name));

        assertEquals(name.toLowerCase().trim(), companyDto.name());
    }

    @Test
    @Transactional
    void deveAtualizarComOsDadosNecessarios() {
        CompanyDto savedDto = companyService.save(new SaveCompanyDto("Company name"));

        String name = " New Company name";
        CompanyDto updatedDto = companyService.update(new UpdateCompanyDto(savedDto.id(), name));

        assertEquals(name.toLowerCase().trim(), updatedDto.name());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String name = "Company name";
        CompanyDto companyDto = companyService.save(new SaveCompanyDto(name));

        assertDoesNotThrow(() -> companyService.delete(companyDto.id()));
    }

}