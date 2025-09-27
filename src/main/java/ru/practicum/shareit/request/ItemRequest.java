package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                      // Уникальный идентификатор запроса
    private String description;           // Текст описания нужной вещи
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requestor;               // Пользователь, создавший запрос
    private Instant created = Instant.now();        // Дата и время создания запроса

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;
        return id != null && id.equals(((ItemRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
