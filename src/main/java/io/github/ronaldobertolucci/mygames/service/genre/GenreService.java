package io.github.ronaldobertolucci.mygames.service.genre;

import io.github.ronaldobertolucci.mygames.model.genre.*;
import io.github.ronaldobertolucci.mygames.model.genre.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository repository;

    public Page<GenreDto> findByNameContaining(String name, Pageable pageable) {
        Page<Genre> genres = repository.findGenresByNameContaining(name, pageable);
        return genres.map(GenreDto::new);
    }

    public List<GenreDto> findByNameContaining(String name) {
        List<Genre> genres = repository.findGenresByNameContaining(name);
        return genres.stream().map(GenreDto::new).toList();
    }

    public Page<GenreDto> findAll(Pageable pageable) {
        Page<Genre> genres = repository.findAll(pageable);
        return genres.map(GenreDto::new);
    }

    public List<GenreDto> findAll() {
        List<Genre> genres = repository.findAll();
        return genres.stream().map(GenreDto::new).toList();
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
