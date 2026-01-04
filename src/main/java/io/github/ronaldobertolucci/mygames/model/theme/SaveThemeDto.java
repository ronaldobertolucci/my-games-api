package io.github.ronaldobertolucci.mygames.model.theme;

import jakarta.validation.constraints.NotBlank;

public record SaveThemeDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
