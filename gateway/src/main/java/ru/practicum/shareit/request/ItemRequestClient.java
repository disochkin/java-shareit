package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(Long requestorId, ItemRequestAddDto itemRequestAddDto) {
        return post("", requestorId, itemRequestAddDto);
    }

    public ResponseEntity<Object> getOwnRequests(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getOtherUserRequests(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId) {
        return get("/" + requestId);
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
//    public ResponseEntity<Object> getItem(long itemId) {
//        return get("/" + itemId);
//    }

//    public ResponseEntity<Object> updateUser(long userId, UserUpdateDto userUpdateDto) {
//        return patch(String.format("/%s",userId), userUpdateDto);
//    }
//
//    public ResponseEntity<Object> deleteUser(long userId) {
//        return delete(String.format("/%s",userId));
//    }

}
