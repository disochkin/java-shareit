package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated

public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping()
    public Collection<Item> getItemFromUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsFromUser(ownerId);
    }

    @GetMapping("/search")
    public Collection<Item> itemSearchByNameOrDescription(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList(); // Возвращаем пустой список, если text пустой
        }
        return itemService.itemSearchByNameOrDescription(text);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @Valid @RequestBody ItemCreateDto itemCreateDto) {
        itemCreateDto.setOwner(ownerId);
        return itemService.create(itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@Valid @PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwner(ownerId);
        return itemService.update(itemUpdateDto);
    }

}
