package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.mygame.MyGameDto;
import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import io.github.ronaldobertolucci.mygames.service.mygame.MyGameService;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyGameService myGameService;

    @GetMapping("/my-games")
    public ResponseEntity listMyGames(@PageableDefault(size = 20, sort = {"username"}) Pageable pagination) {
        List<MyGameDto> myGames = myGameService.findAll();
        return ResponseEntity.ok(new PageImpl<>(myGames, pagination, myGames.size()));
    }

    @GetMapping("/users")
    public ResponseEntity listUsers(@PageableDefault(size = 20, sort = {"username"}) Pageable pagination) {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(new PageImpl<>(users, pagination, users.size()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity disableUser(@PathVariable Long id) {
        UserDto user = userService.disable(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/enable")
    public ResponseEntity enableUser(@PathVariable Long id) {
        UserDto user = userService.enable(id);
        return ResponseEntity.ok(user);
    }
}