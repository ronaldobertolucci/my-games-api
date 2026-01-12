package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.mygame.MyGameDto;
import io.github.ronaldobertolucci.mygames.model.mygame.MyGamesStatusDto;
import io.github.ronaldobertolucci.mygames.model.mygame.SaveMyGameDto;
import io.github.ronaldobertolucci.mygames.model.mygame.UpdateMyGameDto;
import io.github.ronaldobertolucci.mygames.service.mygame.MyGameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/my-games")
public class MyGameController {

    @Autowired
    private MyGameService service;

    @GetMapping
    public ResponseEntity listByUser(@PageableDefault(size = 20, sort = {"game.title"}) Pageable pagination) {
        Page<MyGameDto> myGames = service.findByUser(pagination, getUsername());
        return ResponseEntity.ok(myGames);
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        MyGameDto dto = service.detail(id, getUsername());
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity save(@RequestBody @Valid SaveMyGameDto data, UriComponentsBuilder builder) {
        MyGameDto dto = service.save(data, getUsername());
        var uri = builder.path("/my-games/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid UpdateMyGameDto data) {
        MyGameDto dto = service.update(data, getUsername());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id, getUsername());
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity disableUser(@PathVariable Long id, @RequestBody @Valid MyGamesStatusDto data) {
        MyGameDto dto = service.updateStatus(id, data, getUsername());
        return ResponseEntity.ok(dto);
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}