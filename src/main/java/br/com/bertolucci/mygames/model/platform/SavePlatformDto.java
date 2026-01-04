package br.com.bertolucci.mygames.model.platform;

import jakarta.validation.constraints.NotBlank;

public record SavePlatformDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
