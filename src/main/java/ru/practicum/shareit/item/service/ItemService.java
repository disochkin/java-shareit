package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemFrontDto create(Long ownerId, ItemAddRequest newItem);

    ItemFrontDto update(Long ownerId, ItemUpdateRequest itemUpdateRequest);

    ItemFrontDto getItemFrontDtoById(Long itemId);

    //ItemFrontDtoWithBookingDate getItemFrontDtoWithBookingDate(Long itemId);

    Collection<ItemFrontDtoWithBookingDate> getOwnerItemsWithBookingDetails(Long ownerId);

    Collection<ItemFrontDto> getItemsFromUser(Long ownerId);

    Collection<ItemFrontDto> itemSearchByNameOrDescription(String text);

    CommentCreatedDto createComment(Long authorId, Long itemId, CommentAddRequest commentAddRequest);

}
