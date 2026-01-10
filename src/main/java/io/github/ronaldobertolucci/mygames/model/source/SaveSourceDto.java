package io.github.ronaldobertolucci.mygames.model.source;

import jakarta.validation.constraints.NotBlank;

public record SaveSourceDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
