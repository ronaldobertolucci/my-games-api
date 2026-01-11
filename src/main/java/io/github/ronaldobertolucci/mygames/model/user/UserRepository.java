package io.github.ronaldobertolucci.mygames.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String email);
    boolean existsByUsername(String email);
}