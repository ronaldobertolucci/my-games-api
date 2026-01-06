package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public ResponseEntity list(@PageableDefault(size = 20, sort = {"username"}) Pageable pagination) {
        Page<UserDto> users = service.findAll(pagination);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity disableUser(@PathVariable Long id) {
        UserDto user = service.disable(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/enable")
    public ResponseEntity enableUser(@PathVariable Long id) {
        UserDto user = service.enable(id);
        return ResponseEntity.ok(user);
    }
}