package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(Long itemId);

    Item create(ItemCreateDto itemCreateDto);

    Item update(ItemCreateDto itemCreateDto);

    Collection<Item> getItemsFromUser(Long ownerId);

    Collection<Item> itemSearchByNameOrDescription(String text);
}
