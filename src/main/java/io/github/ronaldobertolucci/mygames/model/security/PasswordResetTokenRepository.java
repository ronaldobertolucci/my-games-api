package io.github.ronaldobertolucci.mygames.model.security;

import io.github.ronaldobertolucci.mygames.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    PasswordResetToken findByToken(String token);
    
    PasswordResetToken findByUser(User user);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate <= ?1")
    void deleteExpiredTokens(LocalDateTime now);
}