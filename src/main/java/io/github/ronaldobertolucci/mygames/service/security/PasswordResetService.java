package io.github.ronaldobertolucci.mygames.service.security;

import io.github.ronaldobertolucci.mygames.exception.InvalidTokenException;
import io.github.ronaldobertolucci.mygames.exception.TokenExpiredException;
import io.github.ronaldobertolucci.mygames.model.security.PasswordResetToken;
import io.github.ronaldobertolucci.mygames.model.security.PasswordResetTokenRepository;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.name}")
    private String appName;

    @Value("${password.reset.token.expiry.hours}")
    private Integer tokenExpiryHours;

    public void createPasswordResetToken(String email) {
        User user = userRepository.findByUsername(email);
        if (user == null) {
            return; // Por segurança, não revelar que o email não existe
        }

        PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user);
        if (existingToken != null) {
            passwordResetTokenRepository.delete(existingToken);
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        sendResetEmail(user.getUsername(), token);
    }

    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null) {
            throw new InvalidTokenException("Token inválido");
        }

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Token expirado");
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null) {
            throw new InvalidTokenException("Token inválido");
        }

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Token expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    private void sendResetEmail(String email, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        String htmlContent = buildEmailTemplate(resetUrl);

        emailService.sendHtmlEmail(email,
                appName + " - Solicitação de Reset de Senha",
                htmlContent);
    }

    private String buildEmailTemplate(String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;'>" +
                "<h2 style='color: #333; margin-bottom: 20px;'>Reset de Senha</h2>" +
                "<p>Olá</p>" +
                "<p>Você solicitou o reset de sua senha no " + appName + ".</p>" +
                "<p>Clique no botão abaixo para criar uma nova senha:</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetUrl + "' " +
                "style='display: inline-block; padding: 12px 30px; " +
                "background-color: #007bff; color: white; text-decoration: none; " +
                "border-radius: 5px; font-weight: bold;'>Resetar Senha</a>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px;'>Ou copie e cole o link abaixo no seu navegador:</p>" +
                "<p style='color: #007bff; word-break: break-all; font-size: 12px;'>" + resetUrl + "</p>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px;'>Este link expira em " + tokenExpiryHours + " horas.</p>" +
                "<p style='color: #999; font-size: 12px;'>Se você não solicitou este reset, ignore este email.</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 20px;'>Atenciosamente,<br>Equipe " + appName + "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Scheduled(cron = "${password.reset.cleanup.cron}")
    public void purgeExpiredTokens() {
        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}