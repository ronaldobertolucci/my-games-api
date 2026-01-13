package io.github.ronaldobertolucci.mygames.model.mygame;

import io.github.ronaldobertolucci.mygames.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyGameRepository extends JpaRepository<MyGame, Long> {
    List<MyGame> findByUser(User user);
}
