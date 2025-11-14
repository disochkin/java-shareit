package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingQueryState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingTimeFilter;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetBookingOfOwnerByStateTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(100L);
        booking.setItem(item);
        booking.setBooker(new User(2L, "Booker", "booker@mail.com"));
        booking.setStatus(BookingStatus.APPROVED);

    }

    @Test
    @DisplayName("Возвращает все бронирования при запросе со статусом ALL")
    void getBookingOfOwnerByState_All() {
        booking.setStartDate(LocalDateTime.now().minusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(1));

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, null, null))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        verify(bookingRepository).findByOwnerIdAndStatus(1L, null, null);
    }

    @Test
    @DisplayName("Возвращает прошлые бронирования при запросе со статусом PAST")
    void getBookingOfOwnerByState_Past() {
        booking.setStartDate(LocalDateTime.now().minusDays(3));
        booking.setEndDate(LocalDateTime.now().minusDays(1));

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, null, BookingTimeFilter.PAST.name()))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.PAST);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        verify(bookingRepository).findByOwnerIdAndStatus(1L, null, BookingTimeFilter.PAST.name());
    }

    @Test
    @DisplayName("Возвращает текущие бронирования при запросе со статусом CURRENT")
    void getBookingOfOwnerByState_Current() {
        booking.setStartDate(LocalDateTime.now().minusHours(1));
        booking.setEndDate(LocalDateTime.now().plusHours(2));

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, null, BookingTimeFilter.CURRENT.name()))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.CURRENT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        verify(bookingRepository).findByOwnerIdAndStatus(1L, null, BookingTimeFilter.CURRENT.name());
    }

    @Test
    @DisplayName("Возвращает будущие бронирования при запросе со статусом FUTURE")
    void getBookingOfOwnerByState_Future() {
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(3));

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, null, BookingTimeFilter.FUTURE.toString()))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.FUTURE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        verify(bookingRepository).findByOwnerIdAndStatus(1L, null, BookingTimeFilter.FUTURE.toString());
    }

    @Test
    @DisplayName("Возвращает ожидающие подтверждения бронирования при запросе со статусом WAITING")
    void getBookingOfOwnerByState_Waiting() {
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, BookingStatus.WAITING, null))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.WAITING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);

        verify(bookingRepository).findByOwnerIdAndStatus(1L, BookingStatus.WAITING, null);
    }

    @Test
    @DisplayName("Возвращает отклонённые бронирования при запросе со статусом REJECTED")
    void getBookingOfOwnerByState_Rejected() {
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatus(1L, BookingStatus.REJECTED, null))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfOwnerByState(1L, BookingQueryState.REJECTED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);

        verify(bookingRepository).findByOwnerIdAndStatus(1L, BookingStatus.REJECTED, null);
    }


    @Test
    @DisplayName("Бросает исключение, если пользователь не найден")
    void getBookingOfOwnerByState_UserNotFound() {
        when(userRepository.getUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingOfOwnerByState(999L, BookingQueryState.ALL))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=999 не найден");
    }

}
