package io.github.ronaldobertolucci.mygames.model.theme;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findThemesByNameContaining(String name);
    Page<Theme> findThemesByNameContaining(String name, Pageable pageable);
}
