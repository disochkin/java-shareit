package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserAddDto;
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

    @Transactional
    public UserFrontDto create(UserAddDto userAddDto) {
        User createdUser = userRepository.save(UserMapper.userCreateDtoToUser(userAddDto));
        return UserMapper.userToUserFrontDto(createdUser);
    }

    public UserFrontDto getUserFrontDtoById(Long userId) {
        return UserMapper.userToUserFrontDto(getUserById(userId));
    }

    @Transactional
    public UserFrontDto update(Long userId, UserUpdateDto userUpdateDto) {
        User updatedUser = getUserById(userId);
        final Optional<User> userWithEmailOpt = userRepository.findAllByEmail(userUpdateDto.getEmail());
        userWithEmailOpt
                .filter(user -> !user.getId().equals(updatedUser.getId()))
                .ifPresent(id -> {
                    throw new ValidationException(String.format("Пользователь с email=%s уже существует", userUpdateDto.getEmail()));
                });
        Optional.ofNullable(userUpdateDto.getName()).ifPresent(updatedUser::setName);
        Optional.ofNullable(userUpdateDto.getEmail()).ifPresent(updatedUser::setEmail);
        userRepository.save(updatedUser);
        return UserMapper.userToUserFrontDto(updatedUser);
    }

    @Transactional
    public void delete(Long userId) {
        final User userToDelete = getUserById(userId);
        userRepository.delete(userToDelete);
    }
}