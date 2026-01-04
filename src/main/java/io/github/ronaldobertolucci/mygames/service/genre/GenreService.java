package io.github.ronaldobertolucci.mygames.service.genre;

import io.github.ronaldobertolucci.mygames.model.genre.*;
import io.github.ronaldobertolucci.mygames.model.genre.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenreService {

    @Autowired
    private GenreRepository repository;

    public Page<GenreDto> findAll(Pageable pageable) {
        Page<Genre> companies = repository.findAll(pageable);
        return companies.map(GenreDto::new);
    }

    public GenreDto detail(Long id) {
        Genre genre = repository.getReferenceById(id);
        return new GenreDto(genre);
    }

    @Transactional
    public GenreDto save(SaveGenreDto dto) {
        Genre genre = new Genre(dto);
        repository.save(genre);
        return new GenreDto(genre);
    }

    @Transactional
    public GenreDto update(UpdateGenreDto dto) {
        Genre genre = repository.getReferenceById(dto.id());
        genre.update(dto);
        return new GenreDto(genre);
    }

    @Transactional
    public void delete(Long id) {
        Genre genre = repository.getReferenceById(id);
        repository.delete(genre);
    }

}
