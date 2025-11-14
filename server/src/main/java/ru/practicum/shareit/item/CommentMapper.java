package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.CommentNestedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class CommentMapper {
    public static Comment addCommentRequestToComment(User author, Item item, CommentAddDto commentAddDto) {
        Comment comment = new Comment();
        comment.setText(commentAddDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }

    public static CommentCreatedDto commentToCreatedDto(Comment comment) {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setId(comment.getId());
        commentCreatedDto.setCreated(comment.getCreated());
        commentCreatedDto.setText(comment.getText());
        commentCreatedDto.setAuthorName(comment.getAuthor().getName());
        return commentCreatedDto;
    }

    public static CommentNestedDto commentToCommentNestedDto(Comment comment) {
        return new CommentNestedDto(comment.getText());
    }

    public static List<CommentNestedDto> commentToCommentNestedDtoList(List<Comment> commentList) {
        return commentList.stream().map(CommentMapper::commentToCommentNestedDto).toList();
    }


}
