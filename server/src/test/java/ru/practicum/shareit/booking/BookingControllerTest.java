package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingFrontDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String HEADER_USER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        // given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS);
        BookingAddDto requestDto = new BookingAddDto();
        requestDto.setItemId(1L);
        requestDto.setStart(startTime);
        requestDto.setEnd(endTime);
        BookingFrontDto responseDto = new BookingFrontDto();
        responseDto.setId(1L);
        responseDto.setStart(startTime);
        responseDto.setEnd(endTime);
        given(bookingService.create(anyLong(), any(BookingAddDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(startTime.truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(endTime.truncatedTo(ChronoUnit.SECONDS).toString()));
    }

//    @Test
//    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
//        // given
//        LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
//        LocalDateTime endTime = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS);
//        BookingAddDto requestDto = new BookingAddDto();
//        requestDto.setItemId(1L);
//        requestDto.setStart(startTime);
//        requestDto.setEnd(endTime);
//        given(bookingService.approve(1L, 1L, true)).willReturn(dto);
//        // when + then
//        mockMvc.perform(patch("/bookings/1")
//                        .header(HEADER_USER, 1L)
//                        .param("approved", "true"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("APPROVED"));
//    }
//
//    @Test
//    void getBooking_ShouldReturnBooking() throws Exception {
//        // given
//        BookingFrontDto dto = new BookingFrontDto(1L, "WAITING");
//        given(bookingService.getBooking(1L, 1L)).willReturn(dto);
//
//        // when + then
//        mockMvc.perform(get("/bookings/1")
//                        .header(HEADER_USER, 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.status").value("WAITING"));
//    }
//
//    @Test
//    void getBookingsByUser_ShouldReturnList() throws Exception {
//        // given
//        BookingFrontDto dto = new BookingFrontDto(1L, "REJECTED");
//        given(bookingService.getBookingOfUserByState(1L, BookingQueryState.ALL))
//                .willReturn(List.of(dto));
//
//        // when + then
//        mockMvc.perform(get("/bookings")
//                        .header(HEADER_USER, 1L)
//                        .param("state", "ALL"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("REJECTED"));
//    }
//
//    @Test
//    void getBookingsOfOwner_ShouldReturnList() throws Exception {
//        // given
//        BookingFrontDto dto = new BookingFrontDto(5L, "WAITING");
//        given(bookingService.getBookingOfOwnerByState(1L, BookingQueryState.ALL))
//                .willReturn(List.of(dto));
//
//        // when + then
//        mockMvc.perform(get("/bookings/owner")
//                        .header(HEADER_USER, 1L)
//                        .param("state", "ALL"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("WAITING"));
//    }
}