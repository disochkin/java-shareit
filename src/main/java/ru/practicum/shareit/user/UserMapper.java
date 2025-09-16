package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public static User userCreateDtoToUser(UserCreateDto userCreateDto) {
        return new User(
                null,
                userCreateDto.getName(),
                userCreateDto.getEmail()
        );
    }

    public static UserFrontDto userToUserFrontDto(User user) {
        return new UserFrontDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}