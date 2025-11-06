package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestAddDto addDto = new ItemRequestAddDto("Нужна дрель");
        ItemRequestCreateDto createdDto = new ItemRequestCreateDto(
                1L, "Нужна дрель", LocalDateTime.now()
        );

        Mockito.when(itemRequestService.create(eq(1L), any(ItemRequestAddDto.class)))
                .thenReturn(createdDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestWithAnswer req = new ItemRequestWithAnswer(
                1L, "Нужна отвертка", LocalDateTime.now(), List.of()
        );

        Mockito.when(itemRequestService.getOwnRequests(1L))
                .thenReturn(List.of(req));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужна отвертка"));
    }

    @Test
    void getOtherRequests_shouldReturnList() throws Exception {
        ItemRequestWithAnswer req = new ItemRequestWithAnswer(
                2L, "Нужен пылесос", LocalDateTime.now(), List.of()
        );

        Mockito.when(itemRequestService.getOtherUserRequests(1L))
                .thenReturn(List.of(req));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужен пылесос"));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestWithAnswer req = new ItemRequestWithAnswer(
                3L, "Нужен молоток", LocalDateTime.now(), List.of()
        );

        Mockito.when(itemRequestService.getRequestById(3L))
                .thenReturn(req);

        mockMvc.perform(get("/requests/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужен молоток"));
    }
}