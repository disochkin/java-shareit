package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

    public static ItemRequest itemRequestAddDtoToItemRequest(User requestor, ItemRequestAddDto itemRequestAddDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestAddDto.getDescription());
        itemRequest.setCreated(itemRequestAddDto.getCreated());
        itemRequest.setRequester(requestor);
        return itemRequest;
    }

    public static ItemRequestWithAnswer itemRequestToItemRequestWithAnswerDto(ItemRequest itemRequest) {
        ItemRequestWithAnswer itemRequestWithAnswer = new ItemRequestWithAnswer();
        itemRequestWithAnswer.setId(itemRequest.getId());
        itemRequestWithAnswer.setDescription(itemRequest.getDescription());
        itemRequestWithAnswer.setCreated(itemRequest.getCreated());
        itemRequestWithAnswer.setItems(
                itemRequest.getAnswer().stream().map(ItemMapper::itemToItemNestedRequestDto).toList());
        return itemRequestWithAnswer;
    }

    public static ItemRequestCreateDto itemRequestToRequestCreateDto(ItemRequest itemRequest) {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setId(itemRequest.getId());
        itemRequestCreateDto.setDescription(itemRequest.getDescription());
        itemRequestCreateDto.setCreated(itemRequest.getCreated());
        return itemRequestCreateDto;
    }

}
