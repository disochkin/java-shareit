package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.dto.UserFrontDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User existingUser;
    private User anotherUser;
    private User expectedUser;
    private UserFrontDto userFrontDto;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Alice");
        existingUser.setEmail("alice@example.com");

        expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName("Alice");
        expectedUser.setEmail("alice@example.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Bob");
        anotherUser.setEmail("bob@example.com");

        userFrontDto = new UserFrontDto();
        userFrontDto.setId(1L);
        userFrontDto.setName("Alice");
        userFrontDto.setEmail("alice@example.com");
    }

    @Test
    @DisplayName("CreateUser тест - проверка создания пользователя")
    void testCreateUser_Success() {
        // Настройка поведения mock'а репозитория
        UserAddDto userAddDto = new UserAddDto();
        userAddDto.setName("Alice");
        userAddDto.setEmail("alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act (действие)
        UserFrontDto result = userService.create(userAddDto);

        // Assert (утверждения)
        assertEquals("Id совпадает", expectedUser.getId(), result.getId());
        assertEquals("Name совпадает", expectedUser.getName(), result.getName());
        assertEquals("Email совпадает", expectedUser.getEmail(), result.getEmail());
    }


    @Test
    @DisplayName("getUserById — успешное получение пользователя")
    void getUserById_ShouldReturnUser_WhenExists() {
        // given
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(existingUser));

        // when
        UserFrontDto result = userService.getUserFrontDtoById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(userRepository).getUserById(1L);
    }

    @Test
    @DisplayName("getUserById — ошибка, если пользователь не найден")
    void getUserById_ShouldThrowException_WhenNotFound() {
        // given
        when(userRepository.getUserById(99L)).thenReturn(Optional.empty());

        // when
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> userService.getUserFrontDtoById(99L)
        );

        // then
        assertThat(ex.getMessage()).contains("id=99");
        verify(userRepository).getUserById(99L);
    }

    @Test
    @DisplayName("Успешное обновление пользователя")
    void updateUser_Success() {
        // given
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Alice Updated");
        dto.setEmail("alice.updated@example.com");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findAllByEmail("alice.updated@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        UserFrontDto result = userService.update(1L, dto);

        // then
        assertThat(result.getName()).isEqualTo("Alice Updated");
        assertThat(result.getEmail()).isEqualTo("alice.updated@example.com");

        verify(userRepository).save(existingUser);
        verify(userRepository).findAllByEmail("alice.updated@example.com");
    }

    @Test
    @DisplayName("Ошибка при попытке обновить email на уже существующий")
    void updateUser_EmailAlreadyExists_ShouldThrowException() {
        // given
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("bob@example.com");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findAllByEmail("bob@example.com"))
                .thenReturn(Optional.of(anotherUser));

        // when
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userService.update(1L, dto));

        // then
        assertThat(ex.getMessage()).contains("bob@example.com");
        verify(userRepository, never()).save(any());
    }
//
//    @Test
//    void testDeleteUser_Success() {
//        // Arrange (подготовительные шаги)
//        Long userId = 1L;
//
//        // Эмулируем существование пользователя с указанным id
//        User existingUser = new User();
//        existingUser.setId(userId);
//        existingUser.setName("existing_user");
//        existingUser.setEmail("exist@example.com");
//
//        // Настраиваем поведение mock'а репозитория
//        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser)); // findById вернет существующего пользователя
//
//        // Act (действие)
//        userService.delete(userId);
//
//        // Assert (утверждение)
//        verify(userRepository, times(1)).delete(existingUser);}
//
//

    }