package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
}