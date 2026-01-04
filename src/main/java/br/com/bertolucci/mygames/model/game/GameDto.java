package br.com.bertolucci.mygames.model.game;

import br.com.bertolucci.mygames.model.company.CompanyDto;
import br.com.bertolucci.mygames.model.genre.GenreDto;
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
        List<GenreDto> genres
) {
    public GameDto(Game game) {
        this(game.getId(), game.getTitle(), game.getDescription(), game.getReleasedAt(),
                new CompanyDto(game.getCompany()), game.getGenres().stream().map(GenreDto::new).toList());
    }
}
