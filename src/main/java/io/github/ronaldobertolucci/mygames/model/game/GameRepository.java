package io.github.ronaldobertolucci.mygames.model.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findGamesByTitleContaining(String title);

    Page<Game> findGamesByTitleContaining(String title, Pageable pageable);
}