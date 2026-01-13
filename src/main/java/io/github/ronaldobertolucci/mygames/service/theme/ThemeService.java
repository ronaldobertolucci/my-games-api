package io.github.ronaldobertolucci.mygames.service.theme;

import io.github.ronaldobertolucci.mygames.model.theme.*;
import io.github.ronaldobertolucci.mygames.model.theme.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository repository;

    public List<ThemeDto> findAll() {
        List<Theme> companies = repository.findAll();
        return companies.stream().map(ThemeDto::new).toList();
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
