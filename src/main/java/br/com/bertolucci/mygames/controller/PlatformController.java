package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.platform.PlatformDto;
import br.com.bertolucci.mygames.model.platform.SavePlatformDto;
import br.com.bertolucci.mygames.model.platform.UpdatePlatformDto;
import br.com.bertolucci.mygames.service.platform.PlatformService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/platforms")
public class PlatformController {

    @Autowired
    private PlatformService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        Page<PlatformDto> platforms = service.findAll(pagination);
        return ResponseEntity.ok(platforms);
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