package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingFrontDto bookingToFrontBookingDto(Booking booking) {
        BookingFrontDto bookingFrontDto = new BookingFrontDto();
        bookingFrontDto.setId(booking.getId());
        bookingFrontDto.setStart(booking.getStartDate());
        bookingFrontDto.setEnd(booking.getEndDate());
        bookingFrontDto.setStatus(booking.getStatus());
        bookingFrontDto.setBooker(UserMapper.userToUserNestedDto(booking.getBooker()));
        bookingFrontDto.setItem(ItemMapper.itemToItemNestedDto(booking.getItem()));
        return bookingFrontDto;
    }

    public static Booking addBookingRequestToBooking(User booker, Item itemToBook, BookingAddDto bookingAddDto) {
        Booking booking = new Booking();
        booking.setStartDate(bookingAddDto.getStart());
        booking.setEndDate(bookingAddDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(itemToBook);
        booking.setBooker(booker);
        return booking;
    }
}
