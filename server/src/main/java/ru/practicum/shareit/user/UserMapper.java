package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserNestedDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public static User userCreateDtoToUser(UserAddDto userAddDto) {
        return new User(
                null,
                userAddDto.getName(),
                userAddDto.getEmail()
        );
    }

    public static UserFrontDto userToUserFrontDto(User user) {
        return new UserFrontDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserNestedDto userToUserNestedDto(User user) {
        return new UserNestedDto(
                user.getId()
        );
    }
}