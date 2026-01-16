package io.github.ronaldobertolucci.mygames.model.genre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findGenresByNameContaining(String name);

    Page<Genre> findGenresByNameContaining(String name, Pageable pageable);
}
