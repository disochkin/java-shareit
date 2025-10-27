package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                     // Уникальный идентификатор бронирования
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    @Column(name = "start_date")
    private LocalDateTime startDate;         // Дата и время начала бронирования
    @FutureOrPresent(message = "Время завершения бронирования не может быть в прошлом")
    @Column(name = "end_date")
    private LocalDateTime endDate;           // Дата и время окончания бронирования
    @ManyToOne()
    @ToString.Exclude
    private Item item;                   // Вещь, которую бронируют
    @ManyToOne
    private User booker;                // Пользователь, осуществляющий бронирование
    @Enumerated(EnumType.STRING)
    private BookingStatus status;       // Текущий статус бронирования
}
