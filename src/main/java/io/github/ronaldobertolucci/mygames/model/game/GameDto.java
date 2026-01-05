package io.github.ronaldobertolucci.mygames.model.game;

import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.genre.GenreDto;
import io.github.ronaldobertolucci.mygames.model.theme.ThemeDto;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GameDto(
        Long id,
        String title,
        String description,
        LocalDate releasedAt,
        CompanyDto company,
        List<GenreDto> genres,
        List<ThemeDto> themes
) {
    public GameDto(Game game) {
        this(game.getId(), game.getTitle(), game.getDescription(), game.getReleasedAt(),
                new CompanyDto(game.getCompany()), game.getGenres().stream().map(GenreDto::new).toList(),
                game.getThemes().stream().map(ThemeDto::new).toList());
    }
}
