package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemRequestCreateDto create(Long requestorId, ItemRequestAddDto itemRequestAddDto) {
        User requestor = userRepository.getUserById(requestorId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", requestorId)));
        ItemRequest createdItemRequest = itemRequestRepository.save(ItemRequestMapper.itemRequestAddDtoToItemRequest(requestor, itemRequestAddDto));
        return ItemRequestMapper.itemRequestToRequestCreateDto(createdItemRequest);
    }

    public List<ItemRequestWithAnswer> getOwnRequests(Long requestorId) {
        User requestor = userRepository.getUserById(requestorId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", requestorId)));
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requestorId).stream()
                .map(ItemRequestMapper::itemRequestToItemRequestWithAnswerDto).toList();
    }

    public List<ItemRequestWithAnswer> getOtherUserRequests(Long requestorId) {
        User requestor = userRepository.getUserById(requestorId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", requestorId)));
        return itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(requestorId).stream()
                .map(ItemRequestMapper::itemRequestToItemRequestWithAnswerDto).toList();
    }

    public ItemRequestWithAnswer getRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.getItemById(requestId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Запрос с id=%s не найден", requestId)));
        return ItemRequestMapper.itemRequestToItemRequestWithAnswerDto(itemRequest);
    }
}
