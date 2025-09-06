package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User create(User newUser);

    Optional<User> getUserById(Long userId);

    User update(User userId);

    Optional<Long> getUserIdWithEmail(String email);

    void delete(User user);
}
