package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddRequest;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    // Создание запроса на бронирование
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingFrontDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @Valid @RequestBody BookingAddRequest bookingAddRequest) {
        return bookingService.create(bookerId, bookingAddRequest);
    }

    // Подтверждение запроса на бронирование владельцем
    @PatchMapping("{bookingId}")
    public BookingFrontDto approve(@RequestHeader("X-Sharer-User-Id") Long approverId,
                                   @PathVariable Long bookingId,
                                   @RequestParam() boolean approved) {
        return bookingService.approve(approverId, bookingId, approved);
    }

    // Запрос информации по заявке бронирование
    @GetMapping("{bookingId}")
    public BookingFrontDto getBooking(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                      @PathVariable Long bookingId) {
        return bookingService.getBooking(requestorId, bookingId);
    }

    // Запрос списка запросов на бронирование от пользователя c отбором по состоянию
    @GetMapping("")
    public List<BookingFrontDto> getBookingOfUserByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "state", required = false,
                                                                 defaultValue = "ALL") BookingQueryState bookingQueryState) {
        return bookingService.getBookingOfUserByState(userId, bookingQueryState);
    }

    // Запрос списка запросов на бронирование владельцу
    @GetMapping("/owner")
    public List<BookingFrontDto> getBookingOfOwnerByState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                          @RequestParam(value = "state", required = false,
                                                                  defaultValue = "ALL") BookingQueryState bookingQueryState) {
        return bookingService.getBookingOfOwnerByState(ownerId, bookingQueryState);
    }


}
