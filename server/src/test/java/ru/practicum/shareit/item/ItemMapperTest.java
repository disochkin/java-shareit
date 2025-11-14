package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemNestedRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void itemToItemNestedRequestDto_success() {
        // Подготовка данных
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(owner);

        // Вызов метода
        ItemNestedRequestDto dto = ItemMapper.itemToItemNestedRequestDto(item);

        // Проверки
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getIdOwner()).isEqualTo(1L);
    }
}
