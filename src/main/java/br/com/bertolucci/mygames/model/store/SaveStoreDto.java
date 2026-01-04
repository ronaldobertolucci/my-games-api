package br.com.bertolucci.mygames.model.store;

import jakarta.validation.constraints.NotBlank;

public record SaveStoreDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
