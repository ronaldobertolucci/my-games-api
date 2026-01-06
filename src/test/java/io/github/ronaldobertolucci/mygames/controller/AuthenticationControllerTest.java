package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.user.*;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.user.UserAuthenticationService;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfigurations.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserAuthenticationService userAuthenticationService;

    @MockitoBean
    private AuthenticationManager manager;

    @MockitoBean
    private Authentication authentication;

    @Test
    void deveLogarQuandoExistente() throws Exception {
        String requestBody = """
            {
                "username": "user@email.com",
                "password": "123456"
            }""";

        when(manager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new User());
        when(tokenService.generateToken(any())).thenReturn("123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("123"));

    }

    @Test
    void deveRegistrarNovoUsuario() throws Exception {
        String requestBody = """
            {
                "username": "user@email.com",
                "password": "123456"
            }""";

        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("user@email.com", "123456");
        when(userAuthenticationService.register(userRegistrationDto)).thenReturn(new UserDto(1L, "user@email.com", Role.USER, true));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void deveFalharRegistrarNovoUsuarioQuandoJaExistente() throws Exception {
        String requestBody = """
            {
                "username": "user@email.com",
                "password": "123456"
            }""";

        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("user@email.com", "123456");
        when(userAuthenticationService.register(userRegistrationDto)).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
}