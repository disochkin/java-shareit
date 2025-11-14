package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceCreateTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Alice");

        item = new Item();
        item.setId(2L);
        item.setName("Drill");
        item.setAvailable(true);
        item.setOwner(new User(3L, "Bob", "bob@example.com"));
    }

    @Test
    @DisplayName("Успешное создание бронирования")
    void createBooking_ShouldReturnFrontDto() {
        // given
        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(2L);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(3));

        when(itemRepository.getItemById(2L)).thenReturn(Optional.of(item));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking b = invocation.getArgument(0);
                    b.setId(100L);
                    b.setStatus(BookingStatus.WAITING);
                    return b;
                });

        // when
        BookingFrontDto result = bookingService.create(1L, dto);

        // then
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Ошибка — start после end")
    void createBooking_ShouldThrow_WhenStartAfterEnd() {
        // given
        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(2L);
        dto.setStart(LocalDateTime.now().plusHours(5));
        dto.setEnd(LocalDateTime.now().plusHours(1));

        // then
        assertThatThrownBy(() -> bookingService.create(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Время конца бронирования не должно быть меньше времени начала");

        verifyNoInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    @DisplayName("Ошибка — предмет не найден")
    void createBooking_ShouldThrow_WhenItemNotFound() {
        // given
        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(999L);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        when(itemRepository.getItemById(999L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookingService.create(1L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Предмет с id=999 не найден");
    }

    @Test
    @DisplayName("Ошибка — пользователь не найден")
    void createBooking_ShouldThrow_WhenUserNotFound() {
        // given
        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(2L);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        when(itemRepository.getItemById(2L)).thenReturn(Optional.of(item));
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookingService.create(1L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=1 не найден");
    }

    @Test
    @DisplayName("Ошибка — предмет недоступен для бронирования")
    void createBooking_ShouldThrow_WhenItemUnavailable() {
        // given
        item.setAvailable(false);

        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(2L);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        when(itemRepository.getItemById(2L)).thenReturn(Optional.of(item));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));

        // then
        assertThatThrownBy(() -> bookingService.create(1L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("не доступен для бронирования");
    }
}
