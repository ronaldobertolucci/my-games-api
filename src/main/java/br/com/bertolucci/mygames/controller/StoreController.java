package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.store.StoreDto;
import br.com.bertolucci.mygames.model.store.SaveStoreDto;
import br.com.bertolucci.mygames.model.store.UpdateStoreDto;
import br.com.bertolucci.mygames.service.store.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        Page<StoreDto> stores = service.findAll(pagination);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        StoreDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveStoreDto data, UriComponentsBuilder builder) {
        StoreDto dto = service.save(data);
        var uri = builder.path("/stores/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateStoreDto data) {
        StoreDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}