package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserAddRequest;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserFrontDto create(@Valid @RequestBody UserAddRequest userAddRequest) {
        return userService.create(userAddRequest);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserFrontDto getUserFrontDtoById(@Valid @PathVariable Long userId) {
        return userService.getUserFrontDtoById(userId);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserFrontDto update(@Valid @PathVariable Long userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.update(userId, userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @PathVariable Long userId) {
        userService.delete(userId);
    }
}