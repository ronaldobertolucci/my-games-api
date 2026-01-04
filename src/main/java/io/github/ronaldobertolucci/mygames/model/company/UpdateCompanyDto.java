package io.github.ronaldobertolucci.mygames.model.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCompanyDto(
        @NotNull(message = "id é obrigatório")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        String name
) {
}
