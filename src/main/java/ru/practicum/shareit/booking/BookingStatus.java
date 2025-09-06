package ru.practicum.shareit.booking;

// Перечисление статуса бронирования
public enum BookingStatus {
    WAITING,                         // Новое бронирование, ждёт подтверждения
    APPROVED,                        // Бронирование одобрено владельцем
    REJECTED,                        // Бронирование отклонено владельцем
    CANCELED                        // Бронирование отменено инициатором
}
