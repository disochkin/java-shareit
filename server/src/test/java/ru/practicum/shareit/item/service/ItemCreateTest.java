package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemFrontDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemCreateTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void create_whenItemAddDtoWithoutRequest_thenCreateItem() {
        // given
        Long ownerId = 1L;
        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setRequestId(null);

        Item item = new Item();
        User owner = new User();
        ItemFrontDto frontDto = new ItemFrontDto();

        when(userRepository.getUserById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<ItemMapper> itemMapperMock = mockStatic(ItemMapper.class)) {
            itemMapperMock.when(() -> ItemMapper.itemAddDtoToItem(itemAddDto)).thenReturn(item);
            itemMapperMock.when(() -> ItemMapper.itemToFrontItemDto(null, item)).thenReturn(frontDto);

            // when
            ItemFrontDto result = itemService.create(ownerId, itemAddDto);

            // then
            assertEquals(frontDto, result);
            assertEquals(owner, item.getOwner());
            assertNull(item.getItemRequest());

            verify(userRepository).getUserById(ownerId);
            verify(itemRepository).save(item);
        }
    }

    @Test
    void create_whenItemAddDtoWithRequest_thenCreateItemWithRequest() {
        // given
        Long ownerId = 1L;
        Long requestId = 100L;
        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setRequestId(requestId);

        Item item = new Item();
        User owner = new User();
        ItemRequest itemRequest = new ItemRequest();
        ItemFrontDto frontDto = new ItemFrontDto();

        when(userRepository.getUserById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.getItemById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<ItemMapper> itemMapperMock = mockStatic(ItemMapper.class)) {
            itemMapperMock.when(() -> ItemMapper.itemAddDtoToItem(itemAddDto)).thenReturn(item);
            itemMapperMock.when(() -> ItemMapper.itemToFrontItemDto(null, item)).thenReturn(frontDto);

            // when
            ItemFrontDto result = itemService.create(ownerId, itemAddDto);

            // then
            assertEquals(frontDto, result);
            assertEquals(owner, item.getOwner());
            assertEquals(itemRequest, item.getItemRequest());

            verify(userRepository).getUserById(ownerId);
            verify(itemRequestRepository).getItemById(requestId);
            verify(itemRepository).save(item);
        }
    }

    @Test
    void create_whenRequestNotFound_thenThrowException() {
        // given
        Long ownerId = 1L;
        Long requestId = 100L;
        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setRequestId(requestId);

        Item item = new Item();
        User owner = new User();

        when(userRepository.getUserById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.getItemById(requestId)).thenReturn(Optional.empty());

        try (MockedStatic<ItemMapper> itemMapperMock = mockStatic(ItemMapper.class)) {
            itemMapperMock.when(() -> ItemMapper.itemAddDtoToItem(itemAddDto)).thenReturn(item);

            // when & then
            NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                    () -> itemService.create(ownerId, itemAddDto));
            assertTrue(ex.getMessage().contains("Запрос с id=100 не найден"));
        }
    }
}
