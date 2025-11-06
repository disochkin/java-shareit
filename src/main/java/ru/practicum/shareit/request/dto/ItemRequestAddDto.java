package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestAddDto {
    @NotBlank(message = "Запрос не может быть пустым")
    private String description;
    private final LocalDateTime created = LocalDateTime.now();
}
