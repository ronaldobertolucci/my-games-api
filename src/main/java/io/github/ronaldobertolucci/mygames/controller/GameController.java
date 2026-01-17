package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.game.SaveGameDto;
import io.github.ronaldobertolucci.mygames.model.game.UpdateGameDto;
import io.github.ronaldobertolucci.mygames.service.game.GameService;
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
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService service;

    @GetMapping
    public ResponseEntity list(@RequestParam(value="title", required = false) String title,
                               @PageableDefault(size = 20, sort ={"title"}) Pageable pagination) {
        if (title == null) {
            return ResponseEntity.ok(service.findAll(pagination));
        }

        return ResponseEntity.ok(service.findByTitleContaining(title, pagination));
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        GameDto dto = service.detail(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveGameDto data, UriComponentsBuilder builder) {
        GameDto dto = service.save(data);
        var uri = builder.path("/games/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateGameDto data) {
        GameDto dto = service.update(data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/genres/{genreId}")
    public ResponseEntity addGenre(@PathVariable Long id, @PathVariable Long genreId) {
        GameDto dto = service.addGenre(id, genreId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/genres/{genreId}")
    public ResponseEntity removeGenre(@PathVariable Long id, @PathVariable Long genreId) {
        GameDto dto = service.removeGenre(id, genreId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/themes/{themeId}")
    public ResponseEntity addTheme(@PathVariable Long id, @PathVariable Long themeId) {
        GameDto dto = service.addTheme(id, themeId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/themes/{themeId}")
    public ResponseEntity removeTheme(@PathVariable Long id, @PathVariable Long themeId) {
        GameDto dto = service.removeTheme(id, themeId);
        return ResponseEntity.ok(dto);
    }
}