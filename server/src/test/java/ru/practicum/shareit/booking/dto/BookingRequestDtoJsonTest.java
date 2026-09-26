package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void serialize() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2026, 11, 10, 12, 0));
        dto.setEnd(LocalDateTime.of(2026, 11, 12, 12, 0));

        JsonContent<BookingRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-11-10T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-11-12T12:00:00");
    }

    @Test
    void deserialize() throws Exception {
        String content = """
                {
                  "itemId": 5,
                  "start": "2026-11-10T12:00:00",
                  "end": "2026-11-12T12:00:00"
                }
                """;

        BookingRequestDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(5L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 11, 10, 12, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 11, 12, 12, 0));
    }
}