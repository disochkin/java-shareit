package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentNestedDto;
import ru.practicum.shareit.item.model.Comment;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void commentToCommentNestedDto_shouldMapTextCorrectly() {
        // given
        Comment comment = new Comment();
        comment.setText("Отличный товар!");

        // when
        CommentNestedDto dto = CommentMapper.commentToCommentNestedDto(comment);

        // then
        assertNotNull(dto);
        assertEquals("Отличный товар!", dto.getText());
    }

    @Test
    void commentToCommentNestedDto_whenTextIsNull_shouldReturnDtoWithNullText() {
        // given
        Comment comment = new Comment();
        comment.setText(null);

        // when
        CommentNestedDto dto = CommentMapper.commentToCommentNestedDto(comment);

        // then
        assertNotNull(dto);
        assertNull(dto.getText());
    }
}