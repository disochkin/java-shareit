package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private Long id;                     // Уникальный идентификатор бронирования
    private LocalDateTime start;         // Дата и время начала бронирования
    private LocalDateTime end;           // Дата и время окончания бронирования
    private Item item;                   // Вещь, которую бронируют
    private User booker;                 // Пользователь, осуществляющий бронирование
    private BookingStatus status;               // Текущий статус бронирования
}
