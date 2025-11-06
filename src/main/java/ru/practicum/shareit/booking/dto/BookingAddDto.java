package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingAddDto {
    @NotNull(message = "Время начала не может быть пустым")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Время завершения не может быть пустым")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    private LocalDateTime end;
    private Long itemId;
}
