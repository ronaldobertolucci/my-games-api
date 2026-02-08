package io.github.ronaldobertolucci.mygames.model.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetTokenForgotDto(
        @Email
        @NotBlank
        String email
) {
}
