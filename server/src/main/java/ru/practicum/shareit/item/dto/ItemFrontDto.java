package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyDate;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemFrontDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDtoOnlyDate lastBooking;
    private BookingDtoOnlyDate nextBooking;
    private List<CommentNestedDto> comments;
}