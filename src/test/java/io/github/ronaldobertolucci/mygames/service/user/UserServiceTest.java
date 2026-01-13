package io.github.ronaldobertolucci.mygames.service.user;

import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import io.github.ronaldobertolucci.mygames.model.user.UserRegistrationDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void deveListarTodosOsUsuarios() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));

        List<UserDto> users = userService.findAll();

        assertEquals(2, users.size()); // admin cadastrado automaticamente
        assertEquals(username, users.stream().filter(u -> u.role() != Role.ADMIN).findFirst().get().username());
        assertEquals(Role.USER, users.stream().filter(u -> u.role() != Role.ADMIN).findFirst().get().role());
        assertTrue(users.stream().filter(u -> u.role() != Role.ADMIN).findFirst().get().enable());
    }

    @Test
    @Transactional
    void deveCarregarPeloUsername() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));

        UserDetails loaded = userService.loadUserByUsername(username);

        assertEquals(username, loaded.getUsername());
    }
    
    @Test
    @Transactional
    void deveRegistrarComDadosNecessarios() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));

        assertEquals(username, userDto.username());
        assertEquals(Role.USER, userDto.role());
        assertTrue(userDto.enable());
    }

    @Test
    @Transactional
    void deveDesabilitarComDadosNecessarios() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));

        UserDto disabled = userService.disable(userDto.id());

        assertEquals(username, disabled.username());
        assertFalse(disabled.enable());
    }

    @Test
    @Transactional
    void deveHabilitarComDadosNecessarios() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));
        UserDto disabled = userService.disable(userDto.id());
        assertEquals(username, disabled.username());
        assertFalse(disabled.enable());

        UserDto enabled = userService.enable(userDto.id());
        assertEquals(username, enabled.username());
        assertTrue(enabled.enable());
    }

    @Test
    @Transactional
    void deveDeletarExistente() {
        String username = "user@email.com";
        String password = "123456";
        UserDto userDto = userService.register(new UserRegistrationDto(username, password));

        assertDoesNotThrow(() -> userService.delete(userDto.id()));
    }

}