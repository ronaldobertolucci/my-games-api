package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyGameRepository extends JpaRepository<MyGame, Long> {
    List<MyGame> findByUser(User user);

    Page<MyGame> findByUser(User user, Pageable pageable);

    @Query("""
        SELECT m FROM MyGame m
                INNER JOIN m.game g
                INNER JOIN m.user u
                WHERE u.username = :username AND g.title LIKE %:title%""")
    List<MyGame> findMyGamesByUsernameAndGameTitleContaining(@Param("username") String username, @Param("title") String title);

    @Query("""
        SELECT m FROM MyGame m
                INNER JOIN m.game g
                INNER JOIN m.user u
                WHERE u.username = :username AND g.title LIKE %:title%""")
    Page<MyGame> findMyGamesByUsernameAndGameTitleContaining(String username, String title, Pageable pageable);
}
