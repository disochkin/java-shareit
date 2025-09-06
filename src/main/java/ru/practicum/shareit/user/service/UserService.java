package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private User getUserById(Long userId) {
        final Optional<User> userOptional = userRepository.getUserById(userId);
        return userOptional.orElseThrow(() ->
                new NoSuchElementException(String.format("Пользователь с id=%s не найден", userId)));
    }

    public UserFrontDto create(UserCreateDto userCreateDto) {
        User createdUser = userRepository.create(UserMapper.userCreateDtoToUser(userCreateDto));
        return UserMapper.userToUserFrontDto(createdUser);
    }

    public UserFrontDto getUserFrontDtoById(Long userId) {
        return UserMapper.userToUserFrontDto(getUserById(userId));
    }

    public UserFrontDto update(UserUpdateDto userUpdateDto) {
        final User updatedUser = getUserById(userUpdateDto.getId());
        final Optional<Long> userIdWithEmailOpt = userRepository.getUserIdWithEmail(userUpdateDto.getEmail());
        userIdWithEmailOpt
                .filter(id -> !id.equals(userUpdateDto.getId()))
                .ifPresent(id -> {
                    throw new ValidationException(String.format("Пользователь с email=%s уже существует", userUpdateDto.getEmail()));
                });
        Optional.ofNullable(userUpdateDto.getName()).ifPresent(updatedUser::setName);
        Optional.ofNullable(userUpdateDto.getEmail()).ifPresent(updatedUser::setEmail);
        return UserMapper.userToUserFrontDto(userRepository.update(updatedUser));
    }

    public void delete(Long userId) {
        final User userToDelete = getUserById(userId);
        userRepository.delete(userToDelete);
    }
}