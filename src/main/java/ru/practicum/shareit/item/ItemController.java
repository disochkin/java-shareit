package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated

public class ItemController {
    private final ItemService itemService;

    // Запрос краткого описания вещи по id
    @GetMapping("/{id}")
    public ItemFrontDto getItemByIdBrief(@PathVariable Long id) {
        return itemService.getItemFrontDtoById(id);
    }

    // Запрос подробного списка вещей владельца с указанием последнего и ближайщего бронирования
    @GetMapping()
    public Collection<ItemFrontDtoWithBookingDate> getOwnerItemsWithBookingDetails(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getOwnerItemsWithBookingDetails(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemFrontDto> itemSearchByNameOrDescription(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList(); // Возвращаем пустой список, если text пустой
        }
        return itemService.itemSearchByNameOrDescription(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemFrontDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @Valid @RequestBody ItemAddRequest itemAddRequest) {
        return itemService.create(ownerId, itemAddRequest);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemFrontDto update(@Valid @PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @Valid @RequestBody ItemUpdateRequest itemUpdateRequest) {
        itemUpdateRequest.setId(itemId);
        return itemService.update(ownerId, itemUpdateRequest);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentCreatedDto addComment(@Valid @PathVariable Long itemId,
                                        @RequestHeader("X-Sharer-User-Id") Long authorId,
                                        @Valid @RequestBody CommentAddRequest commentAddRequest) {
        return itemService.createComment(authorId, itemId, commentAddRequest);
    }
}
