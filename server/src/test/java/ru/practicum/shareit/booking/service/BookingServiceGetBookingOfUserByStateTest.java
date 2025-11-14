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
class BookingServiceGetBookingOfUserByStateTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User booker;
    private Booking booking;
    private Item item;

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
    @DisplayName("Возвращает все бронирования пользователя при состоянии ALL")
    void getBookingOfUserByState_All() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDateDesc(1L))
                .thenReturn(List.of(booking));

        List<BookingFrontDto> result = bookingService.getBookingOfUserByState(1L, BookingQueryState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        verify(bookingRepository).findByBookerIdOrderByStartDateDesc(1L);
    }

    @Test
    @DisplayName("Бросает исключение, если пользователь не найден")
    void getBookingOfUserByState_UserNotFound() {
        when(userRepository.getUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingOfUserByState(999L, BookingQueryState.ALL))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id=999 не найден");
    }

}
