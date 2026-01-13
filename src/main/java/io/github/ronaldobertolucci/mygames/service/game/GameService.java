package io.github.ronaldobertolucci.mygames.service.game;

import io.github.ronaldobertolucci.mygames.exception.UnprocessableEntity;
import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyRepository;
import io.github.ronaldobertolucci.mygames.model.game.*;
import io.github.ronaldobertolucci.mygames.model.genre.Genre;
import io.github.ronaldobertolucci.mygames.model.genre.GenreRepository;
import io.github.ronaldobertolucci.mygames.model.theme.Theme;
import io.github.ronaldobertolucci.mygames.model.theme.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ThemeRepository themeRepository;

    public List<GameDto> findAll() {
        List<Game> games = gameRepository.findAll();
        return games.stream().map(GameDto::new).toList();
    }

    public GameDto detail(Long id) {
        Game game = gameRepository.getReferenceById(id);
        return new GameDto(game);
    }

    @Transactional
    public GameDto save(SaveGameDto dto) {
        Game game = new Game();
        game.setTitle(dto.title());
        game.setDescription(dto.description());
        game.setReleasedAt(dto.releasedAt());

        setCompany(dto.companyId(), game);
        setGenres(dto.genreIds(), game);
        setThemes(dto.themeIds(), game);

        gameRepository.save(game);

        return new GameDto(game);
    }

    @Transactional
    public GameDto update(UpdateGameDto dto) {
        Game game = gameRepository.getReferenceById(dto.id());
        game.setTitle(dto.title());
        game.setDescription(dto.description());
        game.setReleasedAt(dto.releasedAt());

        setCompany(dto.companyId(), game);
        setGenres(dto.genreIds(), game);
        setThemes(dto.themeIds(), game);

        return new GameDto(game);
    }

    @Transactional
    public void delete(Long id) {
        Game game = gameRepository.getReferenceById(id);
        gameRepository.delete(game);
    }

    @Transactional
    public GameDto addGenre(Long gameId, Long genreId) {
        Game game = gameRepository.getReferenceById(gameId);
        Genre genre = genreRepository.getReferenceById(genreId);
        game.getGenres().add(genre);
        return new GameDto(game);
    }

    @Transactional
    public GameDto removeGenre(Long gameId, Long genreId) {
        Game game = gameRepository.getReferenceById(gameId);
        Genre genre = genreRepository.getReferenceById(genreId);
        game.getGenres().remove(genre);
        return new GameDto(game);
    }

    @Transactional
    public GameDto addTheme(Long gameId, Long themeId) {
        Game game = gameRepository.getReferenceById(gameId);
        Theme theme = themeRepository.getReferenceById(themeId);
        game.getThemes().add(theme);
        return new GameDto(game);
    }

    @Transactional
    public GameDto removeTheme(Long gameId, Long themeId) {
        Game game = gameRepository.getReferenceById(gameId);
        Theme theme = themeRepository.getReferenceById(themeId);
        game.getThemes().remove(theme);
        return new GameDto(game);
    }

    private void setThemes(List<Long> themeIds, Game game) {
        if (themeIds == null || themeIds.isEmpty()) {
            game.setThemes(new HashSet<>());
            return;
        }

        try {
            Set<Theme> themes = new HashSet<>();
            for (Long themeId : themeIds) {
                Theme theme = themeRepository.getReferenceById(themeId);
                theme.toString(); // força o carregamento
                themes.add(theme);
            }
            game.setThemes(themes);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

    private void setGenres(List<Long> genreIds, Game game) {
        if (genreIds == null || genreIds.isEmpty()) {
            game.setGenres(new HashSet<>());
            return;
        }

        try {
            Set<Genre> genres = new HashSet<>();
            for (Long genreId : genreIds) {
                Genre genre = genreRepository.getReferenceById(genreId);
                genre.toString(); // força o carregamento
                genres.add(genre);
            }
            game.setGenres(genres);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

    private void setCompany(Long companyId, Game game) {
        try {
            Company company = companyRepository.getReferenceById(companyId);
            company.toString(); // força o carregamento
            game.setCompany(company);
        } catch (EntityNotFoundException ex) {
            throw new UnprocessableEntity("One or more referenced resources do not exist");
        }
    }

}
