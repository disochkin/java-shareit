package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemFrontDto;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    // Создание запроса на предмет
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestCreateDto create(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                       @Valid @RequestBody ItemRequestAddDto itemRequestAddDto) {
        return itemRequestService.create(requestorId, itemRequestAddDto);
    }

    // "Свои" запросы пользователя
    @GetMapping
    public List<ItemRequestWithAnswer> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemRequestService.getOwnRequests(ownerId);
    }

    // Запросы других пользователей
    @GetMapping("/all")
    public List<ItemRequestWithAnswer> getOtherRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemRequestService.getOtherUserRequests(ownerId);
    }

    // Вывод запроса предметов с ответами по его id
    @GetMapping("/{requestId}")
    public ItemRequestWithAnswer getRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
