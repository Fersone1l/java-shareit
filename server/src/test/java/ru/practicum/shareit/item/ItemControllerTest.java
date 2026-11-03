package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_shouldReturnItem() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Дрель");
        request.setDescription("Мощная");
        request.setAvailable(true);

        ItemDto response = new ItemDto();
        response.setId(1L);
        response.setName("Дрель");
        response.setDescription("Мощная");
        response.setAvailable(true);
        response.setOwnerId(1L);

        when(itemService.create(any(ItemDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        ItemWithBookingsDto response = new ItemWithBookingsDto();
        response.setId(1L);
        response.setName("Дрель");

        when(itemService.getItemById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemsByUser_shouldReturnList() throws Exception {
        ItemWithBookingsDto item = new ItemWithBookingsDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemService.getItemsByUser(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemService.getItemsByContent("дрель")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentRequestDto request = new CommentRequestDto();
        request.setText("Отлично");

        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Отлично");

        when(itemService.addComment(eq(1L), eq(2L), any(CommentRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отлично"));
    }
}