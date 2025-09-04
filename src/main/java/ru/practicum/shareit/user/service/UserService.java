package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.create(user);
    }

    public User getUserById(Long userId) {
        final Optional<User> userOptional = userRepository.getUserById(userId);
        return userOptional.orElseThrow(() ->
                new NoSuchElementException(String.format("Пользователь с id=%s не найден", userId)));
    }

    public User update(UserUpdateDto userUpdateDto) {
        final User existUser = getUserById(userUpdateDto.getId());
        final Optional<Long> userIdWithEmailOpt = userRepository.getUserIdWithEmail(userUpdateDto.getEmail());
        userIdWithEmailOpt
                .filter(id -> !id.equals(userUpdateDto.getId()))
                .ifPresent(id -> {
                    throw new ValidationException(String.format("Пользователь с email=%s уже существует", userUpdateDto.getEmail()));
                });
        existUser.setName((userUpdateDto.getName() != null) ? userUpdateDto.getName() : existUser.getName());
        existUser.setEmail((userUpdateDto.getEmail() != null) ? userUpdateDto.getEmail() : existUser.getEmail());
        return userRepository.update(existUser);
    }

    public void delete(Long userId) {
        final User existUser = getUserById(userId);
        userRepository.delete(existUser);
    }
}