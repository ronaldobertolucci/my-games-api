package io.github.ronaldobertolucci.mygames.model.source;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSourceDto(
        @NotNull(message = "id é obrigatório")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
