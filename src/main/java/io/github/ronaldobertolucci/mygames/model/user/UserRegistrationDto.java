package io.github.ronaldobertolucci.mygames.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationDto(
        @NotBlank @Email
        String username,
        @NotBlank @Size(min = 6)
        String password
) {
}
