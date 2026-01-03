package br.com.bertolucci.mygames.model.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateGenreDto(
        @NotNull(message = "id é obrigatório")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
