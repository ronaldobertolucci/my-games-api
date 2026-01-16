package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.theme.ThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.SaveThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.UpdateThemeDto;
import io.github.ronaldobertolucci.mygames.service.theme.ThemeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/themes")
public class ThemeController {

    @Autowired
    private ThemeService service;

    @GetMapping
    public ResponseEntity list(@RequestParam(value="name", required = false) String name,
                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        Page<ThemeDto> themes;
        Pageable pageable = PageRequest.of(page, size);

        if (name == null) {
            themes = service.findAll(pageable);
            return ResponseEntity.ok(themes);
        }
        
        themes = service.findByNameContaining(name, pageable);
        return ResponseEntity.ok(themes);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        ThemeDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveThemeDto data, UriComponentsBuilder builder) {
        ThemeDto dto = service.save(data);
        var uri = builder.path("/themes/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateThemeDto data) {
        ThemeDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}