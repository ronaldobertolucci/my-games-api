package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.platform.PlatformDto;
import io.github.ronaldobertolucci.mygames.model.platform.SavePlatformDto;
import io.github.ronaldobertolucci.mygames.model.platform.UpdatePlatformDto;
import io.github.ronaldobertolucci.mygames.service.platform.PlatformService;
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
@RequestMapping("/platforms")
public class PlatformController {

    @Autowired
    private PlatformService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        List<PlatformDto> platforms = service.findAll();
        return ResponseEntity.ok(new PageImpl<>(platforms, pagination, platforms.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        PlatformDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SavePlatformDto data, UriComponentsBuilder builder) {
        PlatformDto dto = service.save(data);
        var uri = builder.path("/platforms/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdatePlatformDto data) {
        PlatformDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}