package io.github.ronaldobertolucci.mygames.service.theme;

import io.github.ronaldobertolucci.mygames.model.theme.*;
import io.github.ronaldobertolucci.mygames.model.theme.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository repository;

    public Page<ThemeDto> findAll(Pageable pageable) {
        Page<Theme> companies = repository.findAll(pageable);
        return companies.map(ThemeDto::new);
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
