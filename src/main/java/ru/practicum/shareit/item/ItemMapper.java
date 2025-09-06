package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFrontDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static ItemFrontDto itemToFrontItemDto(Item item) {
        return new ItemFrontDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item createItemDtoToItem(ItemCreateDto itemCreateDto) {
        return new Item(
                null,
                itemCreateDto.getName(),
                itemCreateDto.getDescription(),
                itemCreateDto.getAvailable(),
                itemCreateDto.getOwner(),
                null
        );
    }
}