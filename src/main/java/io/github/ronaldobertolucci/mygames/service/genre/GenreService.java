package io.github.ronaldobertolucci.mygames.service.genre;

import io.github.ronaldobertolucci.mygames.model.genre.*;
import io.github.ronaldobertolucci.mygames.model.genre.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository repository;

    public List<GenreDto> findByNameContaining(String name) {
        List<Genre> companies = repository.findGenresByNameContaining(name);
        return companies.stream().map(GenreDto::new).toList();
    }

    public List<GenreDto> findAll() {
        List<Genre> companies = repository.findAll();
        return companies.stream().map(GenreDto::new).toList();
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
