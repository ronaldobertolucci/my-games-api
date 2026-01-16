package io.github.ronaldobertolucci.mygames.service.company;

import io.github.ronaldobertolucci.mygames.model.company.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository repository;

    public List<CompanyDto> findByNameContaining(String name) {
        List<Company> companies = repository.findCompaniesByNameContaining(name);
        return companies.stream().map(CompanyDto::new).toList();
    }

    public List<CompanyDto> findAll() {
        List<Company> companies = repository.findAll();
        return companies.stream().map(CompanyDto::new).toList();
    }

    public CompanyDto detail(Long id) {
        Company company = repository.getReferenceById(id);
        return new CompanyDto(company);
    }

    @Transactional
    public CompanyDto save(SaveCompanyDto dto) {
        Company company = new Company(dto);
        repository.save(company);
        return new CompanyDto(company);
    }

    @Transactional
    public CompanyDto update(UpdateCompanyDto dto) {
        Company company = repository.getReferenceById(dto.id());
        company.update(dto);
        return new CompanyDto(company);
    }

    @Transactional
    public void delete(Long id) {
        Company company = repository.getReferenceById(id);
        repository.delete(company);
    }

}
