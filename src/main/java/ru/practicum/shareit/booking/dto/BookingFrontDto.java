package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemNestedDto;
import ru.practicum.shareit.user.dto.UserNestedDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingFrontDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemNestedDto item;
    private UserNestedDto booker;
    private BookingStatus status;
}
