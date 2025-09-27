package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingQueryState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingTimeFilter;
import ru.practicum.shareit.booking.dto.BookingAddRequest;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Запрос на бронирование с id=%s не найден", bookingId)));
    }

    public BookingFrontDto getItemFrontDtoWithBookingDate(Long bookingId) {
        return BookingMapper.bookingToFrontBookingDto(getBookingById(bookingId));
    }

    @Transactional
    public BookingFrontDto create(Long bookerId, BookingAddRequest bookingAddRequest) {
        if (bookingAddRequest.getStart().isAfter(bookingAddRequest.getEnd())) {
            throw new ValidationException("Время конца бронирования не должно быть меньше времени начала");
        }
        Item itemToBook = itemRepository.getItemById(bookingAddRequest.getItemId())
                .orElseThrow(() -> new NoSuchElementException(String.format(String.format("Предмет с id=%s не найден", bookingAddRequest.getItemId()))));
        User booker = userRepository.getUserById(bookerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", bookerId)));

        if (!itemToBook.isAvailable()) {
            throw new IllegalStateException(String.format("Предмет с id=%s не доступен для бронирования", bookingAddRequest.getItemId()));
        }
        Booking booking = BookingMapper.addBookingRequestToBooking(booker, itemToBook, bookingAddRequest);
        bookingRepository.save(booking);
        return BookingMapper.bookingToFrontBookingDto(booking);
    }

    @Transactional
    public BookingFrontDto approve(Long approverId, Long bookingId, boolean isApprove) {
        Booking booking = bookingRepository.getItemById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Запрос на бронирование с id=%s не найден", bookingId)));
        if (!Objects.equals(booking.getItem().getOwner().getId(), approverId)) {
            throw new AccessViolationException("Изменять статус бронирования может только владелец вещи");
        }
        booking.setStatus(isApprove ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.bookingToFrontBookingDto(booking);
    }

    public BookingFrontDto getBooking(Long requestorId, Long bookingId) {
        Booking booking = bookingRepository.getItemById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Запрос на бронирование с id=%s не найден", bookingId)));
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        boolean isBooker = Objects.equals(bookerId, requestorId);
        boolean isOwner = Objects.equals(ownerId, requestorId);
        if (!(isBooker || isOwner)) {
            throw new AccessViolationException("Запрашивать информацию о бронирования может только владелец вещи " +
                    "или создатель запроса на бронирование");
        }
        return BookingMapper.bookingToFrontBookingDto(booking);
    }

    // Запросы на бронирование владельцу
    public List<BookingFrontDto> getBookingOfOwnerByState(Long ownerId, BookingQueryState bookingQueryState) {
        User owner = userRepository.getUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", ownerId)));
        switch (bookingQueryState) {
            case ALL -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, null, null).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case PAST -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, null, BookingTimeFilter.PAST.name()).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case CURRENT -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, null, BookingTimeFilter.CURRENT.name()).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case FUTURE -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, null, BookingTimeFilter.FUTURE.toString()).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case WAITING -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, null).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case REJECTED -> {
                return bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, null).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            default -> throw new ValidationException("Недопустимый статус: " + bookingQueryState);
        }
    }

    // Запросы на бронирование пользователя
    public List<BookingFrontDto> getBookingOfUserByState(Long bookerId, BookingQueryState bookingQueryState) {
        User booker = userRepository.getUserById(bookerId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с id=%s не найден", bookerId)));
        switch (bookingQueryState) {
            case ALL -> {
                return bookingRepository.findByBookerIdOrderByStartDateDesc(bookerId).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case CURRENT -> {
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now, now).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case PAST -> {
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, now).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case FUTURE -> {
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, now).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case WAITING -> {
                return bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            case REJECTED -> {
                return bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::bookingToFrontBookingDto)
                        .toList();
            }
            default -> throw new ValidationException("Недопустимый статус: " + bookingQueryState);
        }
    }
}
