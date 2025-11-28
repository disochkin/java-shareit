package ru.practicum.shareit.booking;

// Перечисление статуса бронирования
public enum BookingQueryState {
    ALL,                         // Все бронирования
    PAST,                        // Завершённые бронирования
    CURRENT,                     // Бронирования со startDate раньше текущего и endDate позже
    FUTURE,                      // Бронирования с startDate в будущем
    WAITING,                     // Ожидающие подтверждения бронирования
    REJECTED                     // Отклоненные бронирования
}
