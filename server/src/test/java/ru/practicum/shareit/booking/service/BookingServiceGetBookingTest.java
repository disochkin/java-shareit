package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetBookingTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);
        owner.setName("Owner");

        booker = new User();
        booker.setId(20L);
        booker.setName("Booker");

        item = new Item();
        item.setId(100L);
        item.setName("Drill");
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    @DisplayName("getBooking — возвращает бронирование для владельца вещи")
    void getBooking_ShouldReturnBooking_WhenRequesterIsOwner() {
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));

        BookingFrontDto result = bookingService.getBooking(10L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).getItemById(1L);
    }

    @Test
    @DisplayName("getBooking — возвращает бронирование для автора запроса (booker)")
    void getBooking_ShouldReturnBooking_WhenRequesterIsBooker() {
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));

        BookingFrontDto result = bookingService.getBooking(20L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getBooking — выбрасывает исключение, если бронирование не найдено")
    void getBooking_ShouldThrow_WhenBookingNotFound() {
        when(bookingRepository.getItemById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBooking(10L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Запрос на бронирование с id=99 не найден");
    }

    @Test
    @DisplayName("getBooking — выбрасывает исключение, если пользователь не владелец и не booker")
    void getBooking_ShouldThrow_WhenRequesterNotOwnerOrBooker() {
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(999L, 1L))
                .isInstanceOf(AccessViolationException.class)
                .hasMessageContaining("Запрашивать информацию о бронирования может только владелец вещи");
    }
}
