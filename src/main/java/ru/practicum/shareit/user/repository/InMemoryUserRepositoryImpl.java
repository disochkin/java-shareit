package ru.practicum.shareit.user.repository;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long generatorId = 0L;

    private void addId(User user) {
        final Long id = ++generatorId;
        user.setId(id);
    }

    public Optional<Long> getUserIdWithEmail(String email) {
        return users.keySet()
                .stream()
                .filter(id -> users.get(id).getEmail().equals(email))
                .findFirst();
    }

    public User create(User newUser) {
        if (getUserIdWithEmail(newUser.getEmail()).isPresent()) {
            throw new ValidationException(String.format("Пользователь с email %s уже зарегистрирован",
                    newUser.getEmail()));
        }
        addId(newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public void delete(User user) {
        users.remove(user.getId());
    }
}