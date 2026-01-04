package br.com.bertolucci.mygames.controller;

import br.com.bertolucci.mygames.model.genre.GenreDto;
import br.com.bertolucci.mygames.model.genre.SaveGenreDto;
import br.com.bertolucci.mygames.model.genre.UpdateGenreDto;
import br.com.bertolucci.mygames.service.genre.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService service;

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"name"}) Pageable pagination) {
        Page<GenreDto> genres = service.findAll(pagination);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        GenreDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveGenreDto data, UriComponentsBuilder builder) {
        GenreDto dto = service.save(data);
        var uri = builder.path("/genres/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateGenreDto data) {
        GenreDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}