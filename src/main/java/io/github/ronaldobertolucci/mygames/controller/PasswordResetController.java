package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.model.security.PasswordResetTokenResetDto;
import io.github.ronaldobertolucci.mygames.model.security.PasswordResetTokenForgotDto;
import io.github.ronaldobertolucci.mygames.service.security.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestBody @Valid PasswordResetTokenForgotDto dto) {
        passwordResetService.createPasswordResetToken(dto.email());
        return ResponseEntity.ok("Email de reset enviado com sucesso");
    }

    @GetMapping("/reset/validate")
    public ResponseEntity validateToken(@RequestParam String token) {
        passwordResetService.validatePasswordResetToken(token);
        return ResponseEntity.ok("Token válido");
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@RequestBody @Valid PasswordResetTokenResetDto dto) {
        passwordResetService.resetPassword(dto.token(), dto.newPassword());
        return ResponseEntity.ok("Senha alterada com sucesso");
    }
}
