package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final UserRepository userRepository;

    public Item getItemById(Long itemId) {
        final Optional<Item> itemOptional = itemRepository.getItemById(itemId);
        return itemOptional.orElseThrow(() ->
                new NoSuchElementException(String.format("Вещь с id=%s не найдена", itemId)));
    }

    public Collection<Item> getItemsFromUser(Long ownerId) {
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        return itemRepository.getItemsFromUser(ownerId);
    }

    @Override
    public Collection<Item> itemSearchByNameOrDescription(String text) {
        return itemRepository.itemSearchByNameOrDescription(text);
    }

    public Item create(ItemCreateDto itemCreateDto) {
        Long ownerId = itemCreateDto.getOwner();
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        return itemRepository.create(ItemMapper.createItemToItem(itemCreateDto));
    }

    public Item update(ItemUpdateDto itemUpdateDto) {
        final Item existItem = getItemById(itemUpdateDto.getId());
        Long ownerId = itemUpdateDto.getOwner();
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден",ownerId)));

        if (!Objects.equals(itemUpdateDto.getOwner(), existItem.getOwner())) {
            throw new AccessViolationException("Доступ ограничен!");
        }

        existItem.setName((itemUpdateDto.getName() != null) ? itemUpdateDto.getName() : existItem.getName());
        existItem.setDescription((itemUpdateDto.getDescription() != null) ? itemUpdateDto.getDescription() : existItem.getDescription());
        existItem.setAvailable((itemUpdateDto.getAvailable() != null) ? itemUpdateDto.getAvailable() : existItem.getAvailable());
        existItem.setOwner((itemUpdateDto.getOwner() != null) ? itemUpdateDto.getOwner() : existItem.getOwner());
        existItem.setItemRequest((itemUpdateDto.getItemRequest() != null) ? itemUpdateDto.getItemRequest() : existItem.getItemRequest());

        return itemRepository.update(ItemMapper.updateItemToItem(itemUpdateDto));
    }
}
