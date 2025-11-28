package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @DisplayName("POST /bookings — создание бронирования")
    void createBooking() throws Exception {
        BookingAddDto dto = new BookingAddDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(5));

        BookingFrontDto response = new BookingFrontDto();
        response.setId(100L);

        Mockito.when(bookingService.create(eq(1L), any(BookingAddDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));

        verify(bookingService).create(eq(1L), any());
    }

    @Test
    @DisplayName("PATCH /bookings/{id} — подтверждение бронирования")
    void approveBooking() throws Exception {
        BookingFrontDto response = new BookingFrontDto();
        response.setId(200L);

        Mockito.when(bookingService.approve(1L, 50L, true))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/50")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200L));

        verify(bookingService).approve(1L, 50L, true);
    }

    @Test
    @DisplayName("GET /bookings/{id} — получить бронирование")
    void getBooking() throws Exception {
        BookingFrontDto response = new BookingFrontDto();
        response.setId(300L);

        Mockito.when(bookingService.getBooking(1L, 300L))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/300")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(300L));

        verify(bookingService).getBooking(1L, 300L);
    }

    @Test
    @DisplayName("GET /bookings — запросы пользователя по state")
    void getBookingOfUserByState() throws Exception {
        BookingFrontDto dto = new BookingFrontDto();
        dto.setId(1L);

        Mockito.when(bookingService.getBookingOfUserByState(1L, BookingQueryState.ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getBookingOfUserByState(1L, BookingQueryState.ALL);
    }

    @Test
    @DisplayName("GET /bookings/owner — запросы владельца по state")
    void getBookingOfOwnerByState() throws Exception {
        BookingFrontDto dto = new BookingFrontDto();
        dto.setId(5L);

        Mockito.when(bookingService.getBookingOfOwnerByState(1L, BookingQueryState.ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L));

        verify(bookingService).getBookingOfOwnerByState(1L, BookingQueryState.ALL);
    }
}
