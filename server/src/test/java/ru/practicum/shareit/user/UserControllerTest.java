package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("create — успешное создание пользователя возвращает 201 и тело ответа")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserAddDto input = new UserAddDto();
        input.setName("Alice");
        input.setEmail("alice@example.com");

        UserFrontDto output = new UserFrontDto();
        output.setId(1L);
        output.setName("Alice");
        output.setEmail("alice@example.com");

        Mockito.when(userService.create(any(UserAddDto.class)))
                .thenReturn(output);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        Mockito.verify(userService).create(any(UserAddDto.class));
    }

//    @Test
//    @DisplayName("create — возвращает 400 при невалидном email")
//    void createUser_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
//        UserAddDto input = new UserAddDto();
//        input.setName("Alice");
//        input.setEmail("invalid-email"); // нарушает @Email
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(input)))
//                .andExpect(status().isBadRequest());
//
//        Mockito.verify(userService, Mockito.never()).create(any());
//    }

    @Test
    @DisplayName("getUserFrontDtoById — возвращает пользователя по ID со статусом 200")
    void getUserById_ShouldReturnUser() throws Exception {
        UserFrontDto dto = new UserFrontDto();
        dto.setId(1L);
        dto.setName("Alice");
        dto.setEmail("alice@example.com");

        Mockito.when(userService.getUserFrontDtoById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        Mockito.verify(userService).getUserFrontDtoById(1L);
    }

    @Test
    @DisplayName("getUserFrontDtoById — возвращает 404, если пользователь не найден")
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        Mockito.when(userService.getUserFrontDtoById(anyLong()))
                .thenThrow(new java.util.NoSuchElementException("Пользователь с id=99 не найден"));

        mockMvc.perform(get("/users/{userId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(userService).getUserFrontDtoById(99L);
    }

    @Test
    @DisplayName("update — успешное обновление пользователя возвращает 200 и обновлённые данные")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Alice Updated");
        updateDto.setEmail("alice.updated@example.com");

        UserFrontDto updated = new UserFrontDto();
        updated.setId(1L);
        updated.setName("Alice Updated");
        updated.setEmail("alice.updated@example.com");

        Mockito.when(userService.update(eq(1L), any(UserUpdateDto.class)))
                .thenReturn(updated);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice.updated@example.com"));

        Mockito.verify(userService).update(eq(1L), any(UserUpdateDto.class));
    }

//    @Test
//    @DisplayName("update — возвращает 400 при ошибке валидации email")
//    void updateUser_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
//        UserUpdateDto updateDto = new UserUpdateDto();
//        updateDto.setEmail("invalid-email"); // нарушает @Email
//
//        mockMvc.perform(patch("/users/{userId}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isBadRequest());
//        Mockito.verify(userService, Mockito.never()).update(any(), any());
//    }

    @Test
    @DisplayName("update — возвращает 500, если email уже существует")
    void updateUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("bob@example.com");

        Mockito.when(userService.update(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new ValidationException("Пользователь с email=bob@example.com уже существует"));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error").value("Пользователь с email=bob@example.com уже существует"));

        Mockito.verify(userService).update(eq(1L), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("update — возвращает 404, если пользователь не найден")
    void updateUser_ShouldReturnNotFound_WhenUserNotFound() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("New Name");

        Mockito.when(userService.update(eq(99L), any(UserUpdateDto.class)))
                .thenThrow(new java.util.NoSuchElementException("Пользователь с id=99 не найден"));

        mockMvc.perform(patch("/users/{userId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=99 не найден"));

        Mockito.verify(userService).update(eq(99L), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("delete — успешное удаление пользователя возвращает 204 No Content")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(eq(userId));
    }

    @Test
    @DisplayName("delete — возвращает 404, если пользователь не найден")
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        Long userId = 99L;
        Mockito.doThrow(new java.util.NoSuchElementException("Пользователь с id=99 не найден"))
                .when(userService).delete(eq(userId));

        mockMvc.perform(delete("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=99 не найден"));

        Mockito.verify(userService).delete(eq(userId));
    }
}
