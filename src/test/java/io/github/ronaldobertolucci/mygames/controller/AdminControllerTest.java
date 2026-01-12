package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfigurations.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @Test
    void deveProbirListarTodosOsUsuariosParaNaoAutenticado() throws Exception {
        Page<UserDto> users = new PageImpl<>(
                List.of(new UserDto(1L, "user@email.com", Role.USER, true)),
                PageRequest.of(0,20),
                1);
        when(userService.findAll(any())).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProbirListarTodosOsUsuariosParaAutenticadoUser() throws Exception {
        Page<UserDto> users = new PageImpl<>(
                List.of(new UserDto(1L, "user@email.com", Role.USER, true)),
                PageRequest.of(0,20),
                1);
        when(userService.findAll(any())).thenReturn(users);

        mockMvc.perform(get("/admin/users")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodosOsUsuariosParaAutenticadoAdmin() throws Exception {
        Page<UserDto> users = new PageImpl<>(
                List.of(new UserDto(1L, "user@email.com", Role.USER, true)),
                PageRequest.of(0,20),
                1);
        when(userService.findAll(any())).thenReturn(users);

        mockMvc.perform(get("/admin/users")
                        .with(user("admin@admin.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("user@email.com"))
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void deveProibirDeletarUsuarioParaNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProibirDeletarUsuarioParaAutenticadoUser() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", 1L)
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDeletarUsuarioParaAutenticadoAdmin() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", 1L)
                        .with(user("user@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveProibirDesabilitarUsuarioParaNaoAutenticado() throws Exception {
        when(userService.disable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, false));

        mockMvc.perform(patch("/admin/users/{id}/disable", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProibirDesabilitarUsuarioParaAutenticadoUser() throws Exception {
        when(userService.disable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, false));

        mockMvc.perform(patch("/admin/users/{id}/disable", 1L)
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveDesabilitarUsuarioParaAutenticadoAdmin() throws Exception {
        when(userService.disable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, false));

        mockMvc.perform(patch("/admin/users/{id}/disable", 1L)
                        .with(user("user@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deveProibirHabilitarUsuarioParaNaoAutenticado() throws Exception {
        when(userService.enable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, true));

        mockMvc.perform(patch("/admin/users/{id}/enable", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProibirHabilitarUsuarioParaAutenticadoUser() throws Exception {
        when(userService.enable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, true));

        mockMvc.perform(patch("/admin/users/{id}/enable", 1L)
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveHabilitarUsuarioParaAutenticadoAdmin() throws Exception {
        when(userService.enable(any())).thenReturn(new UserDto(1L, "user@email.com", Role.USER, true));

        mockMvc.perform(patch("/admin/users/{id}/enable", 1L)
                        .with(user("user@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}