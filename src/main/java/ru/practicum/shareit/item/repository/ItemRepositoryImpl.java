package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl {
    protected HashMap<Long, Item> items = new HashMap<>();
    protected Long generatorId = 0L;

    private void addId(Item item) {
        final Long id = ++generatorId;
        item.setId(id);
    }

    public Optional<Item> getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        } else {
            return Optional.empty();
        }
    }

    public Collection<Item> getItemsFromUser(Long ownerId) {
        return items.values().stream()
                .filter(obj -> Objects.equals(obj.getOwner(), ownerId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Item> itemSearchByNameOrDescription(String text) {
        return items.values().stream()
                .filter(item -> ((item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))) &&
                        (item.getAvailable() == true))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Item create(Item newItem) {
        addId(newItem);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
