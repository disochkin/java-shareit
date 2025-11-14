package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ItemFrontDto itemFrontDto;
    private ItemFrontDtoWithBookingDate itemFrontDtoWithBookingDate;
    private CommentCreatedDto commentCreatedDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();

        itemFrontDto = new ItemFrontDto();
        itemFrontDto.setId(1L);
        itemFrontDto.setName("Drill");
        itemFrontDto.setDescription("Electric drill");

        itemFrontDtoWithBookingDate = new ItemFrontDtoWithBookingDate();
        itemFrontDtoWithBookingDate.setId(1L);
        itemFrontDtoWithBookingDate.setName("Drill");

        commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setId(1L);
        commentCreatedDto.setText("Nice tool");
        commentCreatedDto.setAuthorName("User");
        commentCreatedDto.setCreated(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /items/{id} - получить вещь по id")
    void getItemByIdBrief_success() throws Exception {
        when(itemService.getItemFrontDtoById(1L)).thenReturn(itemFrontDto);

        mockMvc.perform(get("/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService).getItemFrontDtoById(1L);
    }

    @Test
    @DisplayName("GET /items - получить вещи владельца с бронированиями")
    void getOwnerItemsWithBookingDetails_success() throws Exception {
        when(itemService.getOwnerItemsWithBookingDetails(1L)).thenReturn(List.of(itemFrontDtoWithBookingDate));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService).getOwnerItemsWithBookingDetails(1L);
    }

    @Test
    @DisplayName("GET /items/search - поиск вещей")
    void itemSearchByNameOrDescription_success() throws Exception {
        when(itemService.itemSearchByNameOrDescription("drill")).thenReturn(List.of(itemFrontDto));

        mockMvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService).itemSearchByNameOrDescription("drill");
    }

    @Test
    @DisplayName("POST /items - создать вещь")
    void createItem_success() throws Exception {
        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setName("Drill");
        itemAddDto.setDescription("Electric drill");
        itemAddDto.setAvailable(true);

        when(itemService.create(eq(1L), any(ItemAddDto.class))).thenReturn(itemFrontDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemAddDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService).create(eq(1L), any(ItemAddDto.class));
    }

    @Test
    @DisplayName("PATCH /items/{itemId} - обновление вещи")
    void updateItem_success() throws Exception {
        ItemFrontDto updatedItemDto = new ItemFrontDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("Updated Drill"); // <-- важная часть
        updatedItemDto.setDescription("Updated description");
        updatedItemDto.setAvailable(true);

        when(itemService.update(eq(1L), any(ItemUpdateDto.class))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Drill"));

        verify(itemService).update(eq(1L), any(ItemUpdateDto.class));
    }

    @Test
    @DisplayName("POST /items/{itemId}/comment - добавить комментарий")
    void addComment_success() throws Exception {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setId(1L);
        commentCreatedDto.setText("Great drill");
        commentCreatedDto.setAuthorName("John");

        when(itemService.createComment(eq(1L), eq(1L), any(CommentAddDto.class)))
                .thenReturn(commentCreatedDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreatedDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great drill"))
                .andExpect(jsonPath("$.authorName").value("John"));

    }
}
