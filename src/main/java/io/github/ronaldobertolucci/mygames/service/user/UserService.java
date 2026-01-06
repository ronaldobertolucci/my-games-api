package io.github.ronaldobertolucci.mygames.service.user;

import io.github.ronaldobertolucci.mygames.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> companies = repository.findAll(pageable);
        return companies.map(UserDto::new);
    }

    public UserDetails findByUsername(String username) {
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
