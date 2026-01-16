package io.github.ronaldobertolucci.mygames.service.theme;

import io.github.ronaldobertolucci.mygames.model.theme.*;
import io.github.ronaldobertolucci.mygames.model.theme.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository repository;

    public Page<ThemeDto> findByNameContaining(String name, Pageable pageable) {
        Page<Theme> themes = repository.findThemesByNameContaining(name, pageable);
        return themes.map(ThemeDto::new);
    }

    public List<ThemeDto> findByNameContaining(String name) {
        List<Theme> themes = repository.findThemesByNameContaining(name);
        return themes.stream().map(ThemeDto::new).toList();
    }

    public Page<ThemeDto> findAll(Pageable pageable) {
        Page<Theme> themes = repository.findAll(pageable);
        return themes.map(ThemeDto::new);
    }

    public List<ThemeDto> findAll() {
        List<Theme> themes = repository.findAll();
        return themes.stream().map(ThemeDto::new).toList();
    }

    public ThemeDto detail(Long id) {
        Theme theme = repository.getReferenceById(id);
        return new ThemeDto(theme);
    }

    @Transactional
    public ThemeDto save(SaveThemeDto dto) {
        Theme theme = new Theme(dto);
        repository.save(theme);
        return new ThemeDto(theme);
    }

    @Transactional
    public ThemeDto update(UpdateThemeDto dto) {
        Theme theme = repository.getReferenceById(dto.id());
        theme.update(dto);
        return new ThemeDto(theme);
    }

    @Transactional
    public void delete(Long id) {
        Theme theme = repository.getReferenceById(id);
        repository.delete(theme);
    }

}
