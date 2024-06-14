package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(Long userId, Integer from, Integer size) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        if (from != null) {
            parameters.put("from", from);
            sb.append("from={from}&");
        }

        if (size != null) {
            parameters.put("size", size);
            sb.append("size={size}");
        }

        return get("?" + sb, userId, parameters);
    }

    public ResponseEntity<Object> searchForItem(Long userId, String text, Integer from, Integer size) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", text);

        if (from != null) {
            parameters.put("from", from);
            sb.append("from={from}&");
        }

        if (size != null) {
            parameters.put("size", size);
            sb.append("size={size}");
        }

        return get("/search?text={text}&" + sb, userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
