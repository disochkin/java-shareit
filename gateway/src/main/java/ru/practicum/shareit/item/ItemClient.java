package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.ItemAddDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemAddDto itemAddDto) {
        return post("", ownerId, itemAddDto);
    }

    public ResponseEntity<Object> createComment(Long authorId, Long itemId, CommentAddDto commentAddDto) {
        return post("/" + itemId + "/comment", authorId, commentAddDto);
    }

    //    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", state.name(),
//                "from", from,
//                "size", size
//        );
//        return get("?state={state}&from={from}&size={size}", userId, parameters);
//    }
//
//
//    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
//        return post("", userId, requestDto);
//    }
//
    public ResponseEntity<Object> getItem(long itemId) {
        return get("/" + itemId);
    }

//    public ResponseEntity<Object> updateUser(long userId, UserUpdateDto userUpdateDto) {
//        return patch(String.format("/%s",userId), userUpdateDto);
//    }
//
//    public ResponseEntity<Object> deleteUser(long userId) {
//        return delete(String.format("/%s",userId));
//    }

}
