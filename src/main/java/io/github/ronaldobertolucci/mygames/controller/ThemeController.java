package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.theme.ThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.SaveThemeDto;
import io.github.ronaldobertolucci.mygames.model.theme.UpdateThemeDto;
import io.github.ronaldobertolucci.mygames.service.theme.ThemeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    @Autowired
    private ThemeService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        List<ThemeDto> themes = service.findAll();
        return ResponseEntity.ok(new PageImpl<>(themes, pagination, themes.size()));
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