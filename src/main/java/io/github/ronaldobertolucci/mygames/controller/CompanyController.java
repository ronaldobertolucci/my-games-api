package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.company.SaveCompanyDto;
import io.github.ronaldobertolucci.mygames.model.company.UpdateCompanyDto;
import io.github.ronaldobertolucci.mygames.service.company.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService service;

    @GetMapping
    public ResponseEntity list(@RequestParam(value="name", required = false) String name,
                               @PageableDefault(size = 20, sort ={"name"}) Pageable pagination) {
        if (name == null) {
            return ResponseEntity.ok(service.findAll(pagination));
        }
        
        return ResponseEntity.ok(service.findByNameContaining(name, pagination));
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