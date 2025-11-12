package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.ItemAddDto;


@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemAddDto itemAddDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Creating item {}, itemId={}", itemAddDto);
        return itemClient.createItem(ownerId, itemAddDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Get itemId={}", itemId);
        return itemClient.getItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @Valid @RequestBody CommentAddDto commentAddDto) {
        return itemClient.createComment(authorId, itemId, commentAddDto);
    }
}
