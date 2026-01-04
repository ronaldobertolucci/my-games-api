package io.github.ronaldobertolucci.mygames.model.theme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateThemeDto(
        @NotNull(message = "id é obrigatório")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
