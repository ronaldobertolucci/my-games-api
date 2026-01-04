package io.github.ronaldobertolucci.mygames.model.company;

import jakarta.validation.constraints.NotBlank;

public record SaveCompanyDto(
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
