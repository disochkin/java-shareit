package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@mail.com");

        request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Нужен молоток");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        // Инициализируем пустой список ответов
        request.setAnswer(List.of());
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Создание запроса — успешно")
    void create_success() {
        ItemRequestAddDto dto = new ItemRequestAddDto();
        dto.setDescription("Нужен молоток");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestCreateDto result = itemRequestService.create(1L, dto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("Нужен молоток");
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("Создание запроса — пользователь не найден")
    void create_userNotFound() {
        ItemRequestAddDto dto = new ItemRequestAddDto();
        dto.setDescription("Нужен молоток");

        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.create(1L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    // -------------------------------------------------------------------------
    // GET OWN REQUESTS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getOwnRequests — успешно")
    void getOwnRequests_success() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));

        List<ItemRequestWithAnswer> result = itemRequestService.getOwnRequests(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getOwnRequests — пользователь не найден")
    void getOwnRequests_userNotFound() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getOwnRequests(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    // -------------------------------------------------------------------------
    // GET OTHER USER REQUESTS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getOtherUserRequests — успешно")
    void getOtherUserRequests_success() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));

        List<ItemRequestWithAnswer> result = itemRequestService.getOtherUserRequests(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getOtherUserRequests — пользователь не найден")
    void getOtherUserRequests_userNotFound() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getOtherUserRequests(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    // -------------------------------------------------------------------------
    // GET REQUEST BY ID
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getRequestById — успешно")
    void getRequestById_success() {
        when(itemRequestRepository.getItemById(10L)).thenReturn(Optional.of(request));

        ItemRequestWithAnswer response = itemRequestService.getRequestById(10L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getDescription()).isEqualTo("Нужен молоток");
    }

    @Test
    @DisplayName("getRequestById — запрос не найден")
    void getRequestById_notFound() {
        when(itemRequestRepository.getItemById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getRequestById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Запрос с id=99 не найден");
    }
}
