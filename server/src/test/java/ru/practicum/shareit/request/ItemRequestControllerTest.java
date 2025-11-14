package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
    }


    @Test
    @DisplayName("POST /requests — успешно создаёт запрос")
    void createRequest() throws Exception {
        ItemRequestAddDto addDto = new ItemRequestAddDto();
        addDto.setDescription("Нужна дрель");

        ItemRequestCreateDto responseDto = new ItemRequestCreateDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");

        when(itemRequestService.create(eq(10L), any(ItemRequestAddDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    @DisplayName("GET /requests — возвращает свои запросы пользователя")
    void getOwnRequests() throws Exception {
        ItemRequestWithAnswer dto = new ItemRequestWithAnswer();
        dto.setId(1L);
        dto.setDescription("Запрос №1");

        when(itemRequestService.getOwnRequests(5L))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Запрос №1"));
    }

    @Test
    @DisplayName("GET /requests/all — возвращает запросы других пользователей")
    void getOtherUserRequests() throws Exception {
        ItemRequestWithAnswer dto = new ItemRequestWithAnswer();
        dto.setId(2L);
        dto.setDescription("Чужой запрос");

        when(itemRequestService.getOtherUserRequests(5L))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Чужой запрос"));
    }

    @Test
    @DisplayName("GET /requests/{id} — возвращает один запрос по id")
    void getRequestById() throws Exception {
        ItemRequestWithAnswer dto = new ItemRequestWithAnswer();
        dto.setId(100L);
        dto.setDescription("Запрос по id");

        when(itemRequestService.getRequestById(100L))
                .thenReturn(dto);

        mvc.perform(get("/requests/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.description").value("Запрос по id"));
    }
}
