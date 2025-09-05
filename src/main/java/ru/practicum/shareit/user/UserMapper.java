package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User toUser(UserUpdateDto userDto) {
        return new User(
                null,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}