package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(ItemCreateDto newItem);

    Item update(ItemUpdateDto itemUpdateDto);

    Item getItemById(Long itemId);

    Collection<Item> getItemsFromUser(Long ownerId);

    Collection<Item> itemSearchByNameOrDescription(String text);
}
