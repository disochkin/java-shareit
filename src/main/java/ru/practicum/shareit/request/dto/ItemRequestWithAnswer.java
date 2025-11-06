package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemNestedRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestWithAnswer {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemNestedRequestDto> items;
}
