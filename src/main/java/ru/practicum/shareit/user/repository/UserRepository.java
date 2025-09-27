package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //User create(User newUser);

    Optional<User> getUserById(Long userId);

    //User update(User userId);

    Optional<User> findAllByEmail(String email);

    void deleteById(Long userId);
}
