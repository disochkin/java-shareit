package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyDate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@AllArgsConstructor
@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Вещь с id=%s не найдена", itemId)));
    }

    public ItemFrontDto getItemFrontDtoById(Long itemId) {
        List<CommentNestedDto> commentNestedDtos = CommentMapper.commentToCommentNestedDtoList(commentRepository.findAllByItemId(itemId));
        return ItemMapper.itemToFrontItemDto(commentNestedDtos, getItemById(itemId));
    }

    public Collection<ItemFrontDtoWithBookingDate> getOwnerItemsWithBookingDetails(Long ownerId) {
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    Long itemId = item.getId();
                    BookingDtoOnlyDate bookingDtoOnlyDateLast = bookingRepository.getLastBookingById(itemId);
                    BookingDtoOnlyDate bookingDtoOnlyDateNext = bookingRepository.getNextBookingById(itemId);
                    List<CommentNestedDto> commentNestedDtos = CommentMapper.commentToCommentNestedDtoList(commentRepository.findAllByItemId(itemId));
                    ItemFrontDtoWithBookingDate dto = ItemMapper.itemToFrontDtoWithBookingDate(bookingDtoOnlyDateLast,
                            bookingDtoOnlyDateNext, commentNestedDtos, item);
                    return dto;
                })
                .toList();
    }

    public Collection<ItemFrontDto> getItemsFromUser(Long ownerId) {
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    List<CommentNestedDto> commentNestedDtos = CommentMapper.commentToCommentNestedDtoList(commentRepository.findAllByItemId(item.getId()));
                    ItemFrontDto dto = ItemMapper.itemToFrontItemDto(commentNestedDtos, item);
                    return dto;
                })
                .toList();
    }

    @Override
    public Collection<ItemFrontDto> itemSearchByNameOrDescription(String text) {
        return itemRepository.searchAvailableToBooking(text).stream()
                .map(item -> {
                    List<CommentNestedDto> commentNestedDtos = CommentMapper.commentToCommentNestedDtoList(commentRepository.findAllByItemId(item.getId()));
                    ItemFrontDto dto = ItemMapper.itemToFrontItemDto(commentNestedDtos, item);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public ItemFrontDto create(Long ownerId, ItemAddDto itemAddDto) {
        Item createdItem = ItemMapper.itemAddDtoToItem(itemAddDto);
        User owner = userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", ownerId)));
        createdItem.setOwner(owner);

        if (itemAddDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.getItemById(itemAddDto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException(String.format("Запрос с id=%s не найден",
                            itemAddDto.getRequestId())));
            createdItem.setItemRequest(itemRequest);
        }

        itemRepository.save(createdItem);
        return ItemMapper.itemToFrontItemDto(null, createdItem);
    }

    @Transactional
    public ItemFrontDto update(Long ownerId, ItemUpdateDto itemUpdateDto) {
        final Item updatedItem = getItemById(itemUpdateDto.getId());
        userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", ownerId)));

        if (!Objects.equals(ownerId, updatedItem.getOwner().getId())) {
            throw new AccessViolationException("Доступ ограничен!");
        }
        Optional.ofNullable(itemUpdateDto.getName()).ifPresent(updatedItem::setName);
        Optional.ofNullable(itemUpdateDto.getDescription()).ifPresent(updatedItem::setDescription);
        Optional.ofNullable(itemUpdateDto.getAvailable()).ifPresent(updatedItem::setAvailable);
        itemRepository.save(updatedItem);
        return ItemMapper.itemToFrontItemDto(null, updatedItem);

    }

    @Transactional
    public CommentCreatedDto createComment(Long authorId, Long itemId, CommentAddDto commentAddDto) {
        User author = userRepository.getUserById(authorId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id %s не найден", authorId)));
        Item item = getItemById(itemId);
        if (Objects.equals(authorId, item.getOwner().getId())) {
            throw new AccessViolationException("Владелец не может добавлять комментарии");
        }
        List<Long> bookingItemsIdsByUser = bookingRepository.checkApprovedBookingExist(authorId, itemId).stream()
                .map(Booking::getItem)
                .map(Item::getId)
                .toList();
        if (!bookingItemsIdsByUser.contains(itemId)) {
            throw new ValidationException(String.format("Пользователь id=%s не арендовал вещь с id=%s",
                    authorId, itemId));
        }
        List<Comment> existComment = commentRepository.findAllByItemIdAndAuthorId(itemId, authorId);
        if (!existComment.isEmpty()) {
            throw new ValidationException(String.format("Пользователь %s уже оставил коментарий для вещи %s",
                    authorId, itemId));
        }
        Comment comment = CommentMapper.addCommentRequestToComment(author, item, commentAddDto);
        return CommentMapper.commentToCreatedDto(commentRepository.save(comment));
    }
}
