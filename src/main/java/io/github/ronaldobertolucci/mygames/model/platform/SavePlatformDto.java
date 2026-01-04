package io.github.ronaldobertolucci.mygames.model.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SavePlatformDto(
        @NotBlank(message = "Nome é obrigatório")
        String name,
        @NotNull(message = "id da loja é obrigatório")
        Long storeId
) {
}
