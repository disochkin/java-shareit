package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetBookingOfUserByStateTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(100L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(LocalDateTime.now().minusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("ALL — возвращает все бронирования пользователя")
    void getBookingOfUserByState_All() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDateDesc(1L))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.ALL);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdOrderByStartDateDesc(1L);
    }

    @Test
    @DisplayName("CURRENT — активные бронирования на текущий момент")
    void getBookingOfUserByState_Current() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.CURRENT);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("PAST — завершённые бронирования")
    void getBookingOfUserByState_Past() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.PAST);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndEndDateBeforeOrderByStartDateDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("FUTURE — предстоящие бронирования")
    void getBookingOfUserByState_Future() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.FUTURE);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartDateAfterOrderByStartDateDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("WAITING — бронирования в ожидании подтверждения")
    void getBookingOfUserByState_Waiting() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.WAITING);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDateDesc(1L, BookingStatus.WAITING);
    }

    @Test
    @DisplayName("REJECTED — отклонённые бронирования")
    void getBookingOfUserByState_Rejected() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.REJECTED);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDateDesc(1L, BookingStatus.REJECTED);
    }

    @Test
    @DisplayName("Пользователь не найден — выбрасывает NoSuchElementException")
    void getBookingOfUserByState_UserNotFound() {
        when(userRepository.getUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingOfUserByState(999L, BookingQueryState.ALL))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=999 не найден");
    }

//    @Test
//    @DisplayName("Неизвестный статус — выбрасывает ValidationException")
//    void getBookingOfUserByState_InvalidState() {
//        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
//
//        assertThatThrownBy(() -> bookingService.getBookingOfUserByState(1L, null))
//                .isInstanceOf(ValidationException.class)
//                .hasMessageContaining("Недопустимый статус");
//    }
}
