package io.github.ronaldobertolucci.mygames.model.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SaveGameDto(
        @NotBlank(message = "Título do jogo é obrigatório")
        String title,
        String description,
        LocalDate releasedAt,
        @NotNull(message = "id da companhia é obrigatório")
        Long companyId,
        List<Long> genreIds
) {
}
