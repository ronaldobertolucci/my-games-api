package io.github.ronaldobertolucci.mygames.model.genre;

import jakarta.validation.constraints.NotBlank;

public record SaveGenreDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
