package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.exception.InvalidTokenException;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.security.PasswordResetService;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetController.class)
@Import({SecurityConfigurations.class})
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager manager;

    @MockitoBean
    private Authentication authentication;

    @Test
    void deveEnviarQuandoEmailValido() throws Exception {
        // Arrange
        String email = "usuario@example.com";
        String requestBody = """
                {
                    "email": "%s"
                }
                """.formatted(email);

        doNothing().when(passwordResetService).createPasswordResetToken(email);

        // Act & Assert
        mockMvc.perform(post("/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Email de reset enviado com sucesso"));
    }

    @Test
    void deveValidarTokenComSucesso() throws Exception {
        // Arrange
        String token = "token-valido-123";
        doNothing().when(passwordResetService).validatePasswordResetToken(token);

        // Act & Assert
        mockMvc.perform(get("/password/reset/validate")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Token válido"));
    }

    @Test
    void deveResetarSenhaComSucesso() throws Exception {
        // Arrange
        String token = "token-valido-123";
        String newPassword = "NovaSenha@123";
        String requestBody = """
                {
                    "token": "%s",
                    "new_password": "%s"
                }
                """.formatted(token, newPassword);

        doNothing().when(passwordResetService).resetPassword(token, newPassword);

        // Act & Assert
        mockMvc.perform(post("/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha alterada com sucesso"));
    }

    @Test
    void deveRetornarErroQuandoTokenInvalido() throws Exception {
        // Arrange
        doThrow(new InvalidTokenException("Token inválido"))
                .when(passwordResetService).validatePasswordResetToken(null);

        // Act & Assert
        mockMvc.perform(get("/password/reset/validate"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deveRetornarErroQuandoTokenInvalidoNoReset() throws Exception {
        // Arrange
        String token = "token-invalido";
        String newPassword = "NovaSenha@123";
        String requestBody = """
                {
                    "token": "%s",
                    "new_password": "%s"
                }
                """.formatted(token, newPassword);

        doThrow(new InvalidTokenException("Token inválido"))
                .when(passwordResetService).resetPassword(token, newPassword);

        // Act & Assert
        mockMvc.perform(post("/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoEmailNuloNoForgot() throws Exception {
        // Arrange
        String requestBody = "{}"; // JSON sem o campo email

        // Act & Assert
        mockMvc.perform(post("/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(passwordResetService, never()).createPasswordResetToken(anyString());
    }

    @Test
    void deveRetornarErroQuandoTokenNuloNoReset() throws Exception {
        // Arrange
        String requestBody = "{}"; // JSON sem os campos necessários

        // Act & Assert
        mockMvc.perform(post("/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(passwordResetService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    void deveRetornarErroQuandoTokenNuloNoValidate() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/password/reset/validate"))
                .andExpect(status().isBadRequest());

        verify(passwordResetService, never()).validatePasswordResetToken(anyString());
    }
}