package io.github.ronaldobertolucci.mygames.service.user;

import io.github.ronaldobertolucci.mygames.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> users = repository.findAll(pageable);
        return users.map(UserDto::new);
    }

    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return users.stream().map(UserDto::new).toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username);
    }

    @Transactional
    public UserDto register(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.USER);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        return new UserDto(repository.save(user));
    }

    @Transactional
    public UserDto disable(Long id) {
        User user = repository.getReferenceById(id);
        user.setEnabled(false);
        return new UserDto(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = repository.getReferenceById(id);
        repository.delete(user);
    }

    @Transactional
    public UserDto enable(Long id) {
        User user = repository.getReferenceById(id);
        user.setEnabled(true);
        return new UserDto(user);
    }
}
