package io.github.ronaldobertolucci.mygames.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAuthDto(
        @NotBlank @Email
        String username,
        @NotBlank
        String password
) {
}
