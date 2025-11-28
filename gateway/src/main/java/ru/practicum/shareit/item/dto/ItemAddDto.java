package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemAddDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    private String description;
    @NotNull(message = "Поле available не должно быть пустым")
    private Boolean available;
    private Long requestId;
    private Long userId;
}