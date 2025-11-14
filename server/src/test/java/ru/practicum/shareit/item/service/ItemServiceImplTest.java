package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyDate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(owner);
    }

    @Test
    @DisplayName("Возвращает список вещей пользователя с комментариями")
    void getItemsFromUser_success() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);
        item.setOwner(owner);

        // Мокируем возврат пользователя
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));

        // Мокируем возврат списка вещей
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        // Мокируем возврат комментариев (пустой список)
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        // Вызов метода
        Collection<ItemFrontDto> result = itemService.getItemsFromUser(1L);

        // Проверка результата
        assertThat(result).hasSize(1);
        ItemFrontDto dto = result.iterator().next();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Electric drill");
        assertThat(dto.isAvailable()).isTrue();

        // Проверка взаимодействий с моками
        verify(userRepository).getUserById(1L);
        verify(itemRepository).findByOwnerId(1L);
        verify(commentRepository).findAllByItemId(item.getId());
    }

    @Test
    @DisplayName("Бросает исключение, если пользователь не найден")
    void getItemsFromUser_userNotFound() {
        when(userRepository.getUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemsFromUser(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    @DisplayName("Создание вещи успешно")
    void createItem_success() {
        ItemAddDto dto = new ItemAddDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemFrontDto result = itemService.create(1L, dto);

        assertThat(result.getName()).isEqualTo("Drill");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("Обновление вещи успешно")
    void updateItem_success() {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setId(10L);
        updateDto.setName("New Drill");

        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(item)).thenReturn(item);

        ItemFrontDto result = itemService.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("New Drill");
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("Обновление вещи с чужим владельцем бросает AccessViolationException")
    void updateItem_wrongOwner() {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setId(10L);
        User otherUser = new User();
        otherUser.setId(2L);

        item.setOwner(otherUser);
        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> itemService.update(1L, updateDto))
                .isInstanceOf(AccessViolationException.class)
                .hasMessageContaining("Доступ ограничен!");
    }

    @Test
    @DisplayName("Получение вещи по id")
    void getItemFrontDtoById_success() {
        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(10L)).thenReturn(List.of());

        assertThatCode(() -> itemService.getItemFrontDtoById(10L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Создание комментария успешно")
    void createComment_success() {
        CommentAddDto commentDto = new CommentAddDto();
        commentDto.setText("Good item");

        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEndDate(LocalDateTime.now().minusHours(1));

        Comment comment = new Comment();
        comment.setText("Good item");
        comment.setAuthor(booker); // <- добавляем автора
        comment.setItem(item);

        when(userRepository.getUserById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.checkApprovedBookingExist(2L, 10L)).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdAndAuthorId(10L, 2L)).thenReturn(List.of());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        assertThatCode(() -> itemService.createComment(2L, 10L, commentDto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Создание комментария владельцем бросает AccessViolationException")
    void createComment_ownerCannotComment() {
        CommentAddDto commentDto = new CommentAddDto();
        commentDto.setText("Good item");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.createComment(1L, 10L, commentDto))
                .isInstanceOf(AccessViolationException.class)
                .hasMessageContaining("Владелец не может добавлять комментарии");
    }

    @Test
    @DisplayName("Создание комментария без бронирования бросает ValidationException")
    void createComment_notBooked() {
        CommentAddDto commentDto = new CommentAddDto();
        commentDto.setText("Good item");

        User booker = new User();
        booker.setId(2L);

        when(userRepository.getUserById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.getItemById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.checkApprovedBookingExist(2L, 10L)).thenReturn(List.of());

        assertThatThrownBy(() -> itemService.createComment(2L, 10L, commentDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь id=2 не арендовал вещь с id=10");
    }

    @Test
    @DisplayName("Возвращает список вещей владельца с данными бронирования и комментариями")
    void getOwnerItemsWithBookingDetails_success() {
        // Моки
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.getLastBookingById(item.getId())).thenReturn(
                new BookingDtoOnlyDate(100L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.WAITING)
        );
        when(bookingRepository.getNextBookingById(item.getId())).thenReturn(
                new BookingDtoOnlyDate(101L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING)
        );
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        // Вызов метода
        Collection<ItemFrontDtoWithBookingDate> result = itemService.getOwnerItemsWithBookingDetails(1L);

        // Проверки
        assertThat(result).hasSize(1);
        ItemFrontDtoWithBookingDate dto = result.iterator().next();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.isAvailable()).isEqualTo(item.isAvailable());
        assertThat(dto.getLastBooking().getId()).isEqualTo(100L);
        assertThat(dto.getNextBooking().getId()).isEqualTo(101L);

        // Проверка взаимодействий с моками
        verify(userRepository).getUserById(1L);
        verify(itemRepository).findByOwnerId(1L);
        verify(bookingRepository).getLastBookingById(item.getId());
        verify(bookingRepository).getNextBookingById(item.getId());
        verify(commentRepository).findAllByItemId(item.getId());
    }

    @Test
    @DisplayName("Бросает исключение, если пользователь не найден")
    void getOwnerItemsWithBookingDetails_userNotFound() {
        when(userRepository.getUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getOwnerItemsWithBookingDetails(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }


}
