package br.com.bertolucci.mygames.service.game;

import br.com.bertolucci.mygames.model.company.Company;
import br.com.bertolucci.mygames.model.company.CompanyRepository;
import br.com.bertolucci.mygames.model.game.*;
import br.com.bertolucci.mygames.model.genre.Genre;
import br.com.bertolucci.mygames.model.genre.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenreRepository genreRepository;

    public Page<GameDto> findAll(Pageable pageable) {
        Page<Game> games = gameRepository.findAll(pageable);
        return games.map(GameDto::new);
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

        Company company = companyRepository.getReferenceById(dto.companyId());
        game.setCompany(company);

        Set<Genre> genres = new HashSet<>();
        for (Long genreId : dto.genreIds()) {
            Genre genre = genreRepository.getReferenceById(genreId);
            genres.add(genre);
        }
        game.setGenres(genres);
        gameRepository.save(game);

        return new GameDto(game);
    }

    @Transactional
    public GameDto update(UpdateGameDto dto) {
        Game game = gameRepository.getReferenceById(dto.id());
        game.setTitle(dto.title());
        game.setDescription(dto.description());
        game.setReleasedAt(dto.releasedAt());

        Company company = companyRepository.getReferenceById(dto.companyId());
        game.setCompany(company);

        Set<Genre> genres = new HashSet<>();
        for (Long genreId : dto.genreIds()) {
            Genre genre = genreRepository.getReferenceById(genreId);
            genres.add(genre);
        }
        game.setGenres(genres);
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

}
