package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemFrontDto create(Long ownerId, ItemAddDto newItem);

    ItemFrontDto update(Long ownerId, ItemUpdateDto itemUpdateDto);

    ItemFrontDto getItemFrontDtoById(Long itemId);

    Collection<ItemFrontDtoWithBookingDate> getOwnerItemsWithBookingDetails(Long ownerId);

    Collection<ItemFrontDto> getItemsFromUser(Long ownerId);

    Collection<ItemFrontDto> itemSearchByNameOrDescription(String text);

    CommentCreatedDto createComment(Long authorId, Long itemId, CommentAddDto commentAddDto);

}
