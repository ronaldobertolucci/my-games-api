package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.game.SaveGameDto;
import io.github.ronaldobertolucci.mygames.model.game.UpdateGameDto;
import io.github.ronaldobertolucci.mygames.service.game.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"title"}) Pageable pagination) {
        Page<GameDto> games = service.findAll(pagination);
        return ResponseEntity.ok(games);
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

    @PostMapping("/{id}/generos/{genreId}")
    public ResponseEntity addGenre(@PathVariable Long id, @PathVariable Long genreId) {
        GameDto dto = service.addGenre(id, genreId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/generos/{genreId}")
    public ResponseEntity removeGenre(@PathVariable Long id, @PathVariable Long genreId) {
        GameDto dto = service.removeGenre(id, genreId);
        return ResponseEntity.ok(dto);
    }
}