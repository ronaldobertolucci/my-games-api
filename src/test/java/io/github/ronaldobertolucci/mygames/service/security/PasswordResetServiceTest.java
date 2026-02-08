package io.github.ronaldobertolucci.mygames.service.security;

import io.github.ronaldobertolucci.mygames.exception.InvalidTokenException;
import io.github.ronaldobertolucci.mygames.exception.TokenExpiredException;
import io.github.ronaldobertolucci.mygames.model.security.PasswordResetToken;
import io.github.ronaldobertolucci.mygames.model.security.PasswordResetTokenRepository;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    @Captor
    private ArgumentCaptor<String> subjectCaptor;

    @Captor
    private ArgumentCaptor<String> htmlContentCaptor;

    private static final String EMAIL = "usuario@example.com";
    private static final String TOKEN = "token-123-abc";
    private static final String NEW_PASSWORD = "NovaSenha@123";
    private static final String ENCODED_PASSWORD = "encoded-password-hash";
    private static final String FRONTEND_URL = "http://localhost:4200";
    private static final String APP_NAME = "MyGames";
    private static final Integer TOKEN_EXPIRY_HOURS = 24;

    private User user;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "frontendUrl", FRONTEND_URL);
        ReflectionTestUtils.setField(passwordResetService, "appName", APP_NAME);
        ReflectionTestUtils.setField(passwordResetService, "tokenExpiryHours", TOKEN_EXPIRY_HOURS);

        user = new User();
        user.setId(1L);
        user.setUsername(EMAIL);
        user.setPassword("old-password");

        passwordResetToken = new PasswordResetToken(TOKEN, user);
    }

    @Test
    void deveCriarTokenDeResetEEnviarEmailQuandoUsuarioExiste() {
        // Arrange
        when(userRepository.findByUsername(EMAIL)).thenReturn(user);
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString());

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(userRepository, times(1)).findByUsername(EMAIL);
        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());
        verify(emailService, times(1)).sendHtmlEmail(
                eq(EMAIL),
                contains(APP_NAME),
                anyString()
        );

        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertThat(capturedToken.getUser()).isEqualTo(user);
        assertThat(capturedToken.getToken()).isNotNull();
    }

    @Test
    void deveDeletarTokenExistenteAntesDecriarNovoQuandoUsuarioJaPossuiToken() {
        // Arrange
        PasswordResetToken existingToken = new PasswordResetToken("old-token", user);
        when(userRepository.findByUsername(EMAIL)).thenReturn(user);
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(existingToken);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString());

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(passwordResetTokenRepository, times(1)).delete(existingToken);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void naoDeveFazerNadaQuandoUsuarioNaoExiste() {
        // Arrange
        when(userRepository.findByUsername(EMAIL)).thenReturn(null);

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(userRepository, times(1)).findByUsername(EMAIL);
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveEnviarEmailComUrlCorretaEConteudoHtml() {
        // Arrange
        when(userRepository.findByUsername(EMAIL)).thenReturn(user);
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString());

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(emailService, times(1)).sendHtmlEmail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                htmlContentCaptor.capture()
        );

        assertThat(emailCaptor.getValue()).isEqualTo(EMAIL);
        assertThat(subjectCaptor.getValue()).contains(APP_NAME).contains("Reset de Senha");

        String htmlContent = htmlContentCaptor.getValue();
        assertThat(htmlContent).contains("<!DOCTYPE html>");
        assertThat(htmlContent).contains("Reset de Senha");
        assertThat(htmlContent).contains(FRONTEND_URL);
        assertThat(htmlContent).contains("/reset-password?token=");
        assertThat(htmlContent).contains(APP_NAME);
        assertThat(htmlContent).contains(TOKEN_EXPIRY_HOURS.toString());
    }

    @Test
    void deveValidarTokenComSucessoQuandoTokenValidoENaoExpirado() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);

        // Act & Assert - não deve lançar exceção
        passwordResetService.validatePasswordResetToken(TOKEN);

        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
    }

    @Test
    void deveLancarInvalidTokenExceptionQuandoTokenNaoExiste() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordResetService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token inválido");

        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
    }

    @Test
    void deveLancarTokenExpiredExceptionQuandoTokenExpirado() {
        // Arrange
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(expiredToken);

        // Act & Assert
        assertThatThrownBy(() -> passwordResetService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("Token expirado");

        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
        verify(expiredToken, times(1)).isExpired();
    }

    @Test
    void deveResetarSenhaComSucessoQuandoTokenValidoENaoExpirado() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).delete(passwordResetToken);

        // Act
        passwordResetService.resetPassword(TOKEN, NEW_PASSWORD);

        // Assert
        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    void deveLancarInvalidTokenExceptionNoResetQuandoTokenNaoExiste() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordResetService.resetPassword(TOKEN, NEW_PASSWORD))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token inválido");

        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void deveLancarTokenExpiredExceptionNoResetQuandoTokenExpirado() {
        // Arrange
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(expiredToken);

        // Act & Assert
        assertThatThrownBy(() -> passwordResetService.resetPassword(TOKEN, NEW_PASSWORD))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("Token expirado");

        verify(passwordResetTokenRepository, times(1)).findByToken(TOKEN);
        verify(expiredToken, times(1)).isExpired();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void deveDeletarTokenAposResetarSenhaComSucesso() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).delete(passwordResetToken);

        // Act
        passwordResetService.resetPassword(TOKEN, NEW_PASSWORD);

        // Assert
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
    }

    @Test
    void deveLimparTokensExpiradosNaTarefaAgendada() {
        // Arrange
        doNothing().when(passwordResetTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        // Act
        passwordResetService.purgeExpiredTokens();

        // Assert
        verify(passwordResetTokenRepository, times(1)).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void deveChamarDeleteExpiredTokensComDataHoraAtual() {
        // Arrange
        ArgumentCaptor<LocalDateTime> dateTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        doNothing().when(passwordResetTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        // Act
        LocalDateTime before = LocalDateTime.now();
        passwordResetService.purgeExpiredTokens();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        verify(passwordResetTokenRepository, times(1)).deleteExpiredTokens(dateTimeCaptor.capture());

        LocalDateTime capturedDateTime = dateTimeCaptor.getValue();
        assertThat(capturedDateTime).isAfterOrEqualTo(before);
        assertThat(capturedDateTime).isBeforeOrEqualTo(after);
    }

    @Test
    void deveGerarTokenUnicoParaCadaSolicitacao() {
        // Arrange
        when(userRepository.findByUsername(EMAIL)).thenReturn(user);
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString());

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(passwordResetTokenRepository, times(2)).save(tokenCaptor.capture());

        String token1 = tokenCaptor.getAllValues().get(0).getToken();
        String token2 = tokenCaptor.getAllValues().get(1).getToken();

        assertThat(token1).isNotEqualTo(token2);
        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
    }

    @Test
    void deveEncodificarNovaSenhaAntesDesSalvar() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        passwordResetService.resetPassword(TOKEN, NEW_PASSWORD);

        // Assert
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isNotEqualTo(NEW_PASSWORD);
        assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    void deveManterDadosDoUsuarioIntactosAposResetarSenha() {
        // Arrange
        user.setEnabled(true);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        passwordResetService.resetPassword(TOKEN, NEW_PASSWORD);

        // Assert
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(savedUser.getUsername()).isEqualTo(EMAIL);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    void deveIncluirLinkDeResetNoEmailComTokenCorreto() {
        // Arrange
        when(userRepository.findByUsername(EMAIL)).thenReturn(user);
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString());

        // Act
        passwordResetService.createPasswordResetToken(EMAIL);

        // Assert
        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());
        verify(emailService, times(1)).sendHtmlEmail(
                anyString(),
                anyString(),
                htmlContentCaptor.capture()
        );

        // O token é gerado aleatoriamente pelo serviço
        String generatedToken = tokenCaptor.getValue().getToken();
        String htmlContent = htmlContentCaptor.getValue();
        String expectedUrl = FRONTEND_URL + "/reset-password?token=" + generatedToken;

        assertThat(htmlContent).contains(expectedUrl);
        assertThat(generatedToken).isNotNull();
        assertThat(generatedToken).isNotEmpty();
    }
}