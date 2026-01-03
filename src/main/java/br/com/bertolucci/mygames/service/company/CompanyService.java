package br.com.bertolucci.mygames.service.company;

import br.com.bertolucci.mygames.model.company.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository repository;

    public Page<CompanyDto> findAll(Pageable pageable) {
        Page<Company> companies = repository.findAll(pageable);
        return companies.map(CompanyDto::new);
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
