package io.github.ronaldobertolucci.mygames.controller;

import io.github.ronaldobertolucci.mygames.config.security.SecurityConfigurations;
import io.github.ronaldobertolucci.mygames.model.company.CompanyDto;
import io.github.ronaldobertolucci.mygames.model.game.GameDto;
import io.github.ronaldobertolucci.mygames.model.mygame.MyGameDto;
import io.github.ronaldobertolucci.mygames.model.mygame.Status;
import io.github.ronaldobertolucci.mygames.model.platform.PlatformDto;
import io.github.ronaldobertolucci.mygames.model.source.SourceDto;
import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import io.github.ronaldobertolucci.mygames.service.mygame.MyGameService;
import io.github.ronaldobertolucci.mygames.service.security.TokenService;
import io.github.ronaldobertolucci.mygames.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

    @MockitoBean
    private MyGameService myGameService;

    @Test
    void deveProbirListarMeusJogosParaNaoAutenticado() throws Exception {
        GameDto gameDto = new GameDto(1L, "game title", "game description", LocalDate.parse("2026-01-01"),
                new CompanyDto(1L, "company name"), List.of(), List.of());
        PlatformDto platformDto = new PlatformDto(1L, "platform name");
        SourceDto sourceDto = new SourceDto(1L, "source name");
        List<MyGameDto> myGames = List.of(new MyGameDto(1L, 1L, gameDto, platformDto, sourceDto, Status.COMPLETED));
        when(myGameService.findAll(any())).thenReturn(new PageImpl<>(myGames, PageRequest.of(0,20), myGames.size()));

        mockMvc.perform(get("/admin/my-games"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProbirListarTodosMeusJogosParaAutenticadoUser() throws Exception {
        GameDto gameDto = new GameDto(1L, "game title", "game description", LocalDate.parse("2026-01-01"),
                new CompanyDto(1L, "company name"), List.of(), List.of());
        PlatformDto platformDto = new PlatformDto(1L, "platform name");
        SourceDto sourceDto = new SourceDto(1L, "source name");
        List<MyGameDto> myGames = List.of(new MyGameDto(1L, 1L, gameDto, platformDto, sourceDto, Status.COMPLETED));
        when(myGameService.findAll(any())).thenReturn(new PageImpl<>(myGames, PageRequest.of(0,20), myGames.size()));

        mockMvc.perform(get("/admin/my-games")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodosOsMeusJogosParaAutenticadoAdmin() throws Exception {
        GameDto gameDto = new GameDto(1L, "game title", "game description", LocalDate.parse("2026-01-01"),
                new CompanyDto(1L, "company name"), List.of(), List.of());
        PlatformDto platformDto = new PlatformDto(1L, "platform name");
        SourceDto sourceDto = new SourceDto(1L, "source name");
        List<MyGameDto> myGames = List.of(new MyGameDto(1L, 1L, gameDto, platformDto, sourceDto, Status.COMPLETED));
        when(myGameService.findAll(any())).thenReturn(new PageImpl<>(myGames, PageRequest.of(0,20), myGames.size()));

        mockMvc.perform(get("/admin/my-games")
                        .with(user("admin@admin.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].game.title").value("game title"))
                .andExpect(jsonPath("$.content[0].platform.name").value("platform name"))
                .andExpect(jsonPath("$.content[0].source.name").value("source name"))
                .andExpect(jsonPath("$.content[0].status").value(Status.COMPLETED.name()))
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void deveProbirListarTodosOsUsuariosParaNaoAutenticado() throws Exception {
        List<UserDto> users = List.of(new UserDto(1L, "user@email.com", Role.USER, true));
        when(userService.findAll(any())).thenReturn(new PageImpl<>(users, PageRequest.of(0,20), users.size()));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveProbirListarTodosOsUsuariosParaAutenticadoUser() throws Exception {
        List<UserDto> users = List.of(new UserDto(1L, "user@email.com", Role.USER, true));
        when(userService.findAll(any())).thenReturn(new PageImpl<>(users, PageRequest.of(0,20), users.size()));

        mockMvc.perform(get("/admin/users")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarTodosOsUsuariosParaAutenticadoAdmin() throws Exception {
        List<UserDto> users = List.of(new UserDto(1L, "user@email.com", Role.USER, true));
        when(userService.findAll(any())).thenReturn(new PageImpl<>(users, PageRequest.of(0,20), users.size()));

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