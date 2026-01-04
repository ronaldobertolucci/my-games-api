package io.github.ronaldobertolucci.mygames.model.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePlatformDto(
        @NotNull(message = "id é obrigatório")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
