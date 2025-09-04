package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;                      // Уникальный идентификатор запроса
    private String description;           // Текст описания нужной вещи
    private User requestor;               // Пользователь, создавший запрос
    private LocalDateTime created;        // Дата и время создания запроса
}
