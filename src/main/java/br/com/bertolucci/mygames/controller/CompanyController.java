package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.company.CompanyDto;
import br.com.bertolucci.mygames.model.company.SaveCompanyDto;
import br.com.bertolucci.mygames.model.company.UpdateCompanyDto;
import br.com.bertolucci.mygames.service.company.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        Page<CompanyDto> companies = service.findAll(pagination);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        CompanyDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveCompanyDto data, UriComponentsBuilder builder) {
        CompanyDto dto = service.save(data);
        var uri = builder.path("/companies/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateCompanyDto data) {
        CompanyDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}