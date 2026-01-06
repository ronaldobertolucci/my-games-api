package io.github.ronaldobertolucci.mygames.model.user;

public record UserDto(
        Long id,
        String username,
        Role role,
        boolean enable
) {
    public UserDto(User user) {
        this(user.getId(), user.getUsername(), user.getRole(), user.isEnabled());
    }
}
