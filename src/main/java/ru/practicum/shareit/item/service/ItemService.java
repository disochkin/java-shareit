package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFrontDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemFrontDto create(ItemCreateDto newItem);

    ItemFrontDto update(ItemUpdateDto itemUpdateDto);

    ItemFrontDto getItemFrontDtoById(Long itemId);

    Collection<ItemFrontDto> getItemsFromUser(Long ownerId);

    Collection<ItemFrontDto> itemSearchByNameOrDescription(String text);
}
