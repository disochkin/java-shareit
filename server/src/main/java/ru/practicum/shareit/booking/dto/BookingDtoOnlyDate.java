package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

public interface BookingDtoOnlyDate {
    Long getId();

    LocalDateTime getStartDate();

    LocalDateTime getEndDate();

    BookingStatus getStatus();
}
