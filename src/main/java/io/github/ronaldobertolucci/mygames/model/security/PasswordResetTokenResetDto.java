package io.github.ronaldobertolucci.mygames.model.security;

import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PasswordResetTokenResetDto(
        @NotBlank
        String token,
        @NotBlank
        String newPassword
) {
}
