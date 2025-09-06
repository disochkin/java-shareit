package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFrontDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final UserRepository userRepository;

    private Item getItemById(Long itemId) {
        final Optional<Item> itemOptional = itemRepository.getItemById(itemId);
        return itemOptional.orElseThrow(() ->
                new NoSuchElementException(String.format("Вещь с id=%s не найдена", itemId)));
    }

    public ItemFrontDto getItemFrontDtoById(Long itemId) {
        return ItemMapper.itemToFrontItemDto(getItemById(itemId));
    }

    public Collection<ItemFrontDto> getItemsFromUser(Long ownerId) {
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        return itemRepository.getItemsFromUser(ownerId).stream()
                .map(ItemMapper::itemToFrontItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemFrontDto> itemSearchByNameOrDescription(String text) {
        return itemRepository.itemSearchByNameOrDescription(text).stream()
                .map(ItemMapper::itemToFrontItemDto)
                .collect(Collectors.toList());
    }

    public ItemFrontDto create(ItemCreateDto itemCreateDto) {
        Long ownerId = itemCreateDto.getOwner();
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        Item createdItem = itemRepository.create(ItemMapper.createItemDtoToItem(itemCreateDto));
        return ItemMapper.itemToFrontItemDto(createdItem);
    }

    public ItemFrontDto update(ItemUpdateDto itemUpdateDto) {
        final Item updatedItem = getItemById(itemUpdateDto.getId());
        Long ownerId = itemUpdateDto.getOwner();
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));

        if (!Objects.equals(itemUpdateDto.getOwner(), updatedItem.getOwner())) {
            throw new AccessViolationException("Доступ ограничен!");
        }

        Optional.ofNullable(itemUpdateDto.getName()).ifPresent(updatedItem::setName);
        Optional.ofNullable(itemUpdateDto.getDescription()).ifPresent(updatedItem::setDescription);
        Optional.ofNullable(itemUpdateDto.getAvailable()).ifPresent(updatedItem::setAvailable);
        Optional.ofNullable(itemUpdateDto.getOwner()).ifPresent(updatedItem::setOwner);
        Optional.ofNullable(itemUpdateDto.getItemRequest()).ifPresent(updatedItem::setItemRequest);

        return ItemMapper.itemToFrontItemDto(itemRepository.update(updatedItem));
    }
}
