package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.source.SourceDto;
import io.github.ronaldobertolucci.mygames.model.source.SaveSourceDto;
import io.github.ronaldobertolucci.mygames.model.source.UpdateSourceDto;
import io.github.ronaldobertolucci.mygames.service.source.SourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/sources")
public class SourceController {

    @Autowired
    private SourceService service;

    @GetMapping
    public ResponseEntity list(@RequestParam(value="name", required = false) String name,
                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        Page<SourceDto> sources;
        Pageable pageable = PageRequest.of(page, size);

        if (name == null) {
            sources = service.findAll(pageable);
            return ResponseEntity.ok(sources);
        }
        
        sources = service.findByNameContaining(name, pageable);
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        SourceDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveSourceDto data, UriComponentsBuilder builder) {
        SourceDto dto = service.save(data);
        var uri = builder.path("/sources/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateSourceDto data) {
        SourceDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}