package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class BookingDtoOnlyDate {
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BookingStatus status;
}
