package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingAddDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingAddDtoTest {

    @Test
    void testModelFields() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        BookingAddDto dto = new BookingAddDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(100L);

        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(100L, dto.getItemId());
    }
}
