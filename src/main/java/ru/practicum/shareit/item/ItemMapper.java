package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemCreateDto toItemDto(Item item) {
        return new ItemCreateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getItemRequest() != null ? item.getItemRequest() : null
        );
    }

    public static Item updateItemToItem(ItemUpdateDto itemUpdateDto) {
        return new Item(
                itemUpdateDto.getId() != null ? itemUpdateDto.getId() : null,
                itemUpdateDto.getName(),
                itemUpdateDto.getDescription(),
                itemUpdateDto.getAvailable(),
                itemUpdateDto.getOwner(),
                itemUpdateDto.getItemRequest() != null ? itemUpdateDto.getItemRequest() : null
        );
    }

    public static Item createItemToItem(ItemCreateDto itemCreateDto) {
        return new Item(
                null,
                itemCreateDto.getName(),
                itemCreateDto.getDescription(),
                itemCreateDto.getAvailable(),
                itemCreateDto.getOwner(),
                itemCreateDto.getItemRequest() != null ? itemCreateDto.getItemRequest() : null
        );
    }
}