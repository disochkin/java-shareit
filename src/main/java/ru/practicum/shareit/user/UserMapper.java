package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserAddRequest;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserNestedDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public static User userCreateDtoToUser(UserAddRequest userAddRequest) {
        return new User(
                null,
                userAddRequest.getName(),
                userAddRequest.getEmail()
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