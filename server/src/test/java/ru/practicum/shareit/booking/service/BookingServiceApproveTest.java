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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceApproveTest {

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
    @DisplayName("approve — успешное одобрение бронирования владельцем")
    void approveBooking_ShouldSetApprovedStatus() {
        // given
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        BookingFrontDto result = bookingService.approve(10L, 1L, true);

        // then
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("approve — успешный отказ бронирования владельцем")
    void approveBooking_ShouldSetRejectedStatus() {
        // given
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        BookingFrontDto result = bookingService.approve(10L, 1L, false);

        // then
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("approve — выбрасывает исключение, если бронирование не найдено")
    void approveBooking_ShouldThrow_WhenBookingNotFound() {
        // given
        when(bookingRepository.getItemById(99L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookingService.approve(10L, 99L, true))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Запрос на бронирование с id=99 не найден");
    }

    @Test
    @DisplayName("approve — выбрасывает ошибку, если действие выполняет не владелец вещи")
    void approveBooking_ShouldThrow_WhenNotOwner() {
        // given
        when(bookingRepository.getItemById(1L)).thenReturn(Optional.of(booking));

        // then
        assertThatThrownBy(() -> bookingService.approve(999L, 1L, true))
                .isInstanceOf(AccessViolationException.class)
                .hasMessageContaining("Изменять статус бронирования может только владелец вещи");

        verify(bookingRepository, never()).save(any());
    }
}
