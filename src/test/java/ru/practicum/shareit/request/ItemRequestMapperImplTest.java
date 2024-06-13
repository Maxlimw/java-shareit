package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperImplTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = createSampleItemRequest();

        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(itemRequest.getItems().size(), itemRequestDto.getItems().size());
        assertEquals(itemRequest.getRequester().getId(), itemRequestDto.getRequester().getId());
        assertEquals(itemRequest.getRequester().getName(), itemRequestDto.getRequester().getName());
        assertEquals(itemRequest.getRequester().getEmail(), itemRequestDto.getRequester().getEmail());
    }

    @Test
    void toItemRequest() {
        ItemRequestDto itemRequestDto = createSampleItemRequestDto();

        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
        assertEquals(itemRequestDto.getItems().size(), itemRequest.getItems().size());
        assertEquals(itemRequestDto.getRequester().getId(), itemRequest.getRequester().getId());
        assertEquals(itemRequestDto.getRequester().getName(), itemRequest.getRequester().getName());
        assertEquals(itemRequestDto.getRequester().getEmail(), itemRequest.getRequester().getEmail());
    }

    private ItemRequest createSampleItemRequest() {
        User requester = new User(1L, "John Doe", "john.doe@example.com");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description of Item 1");
        item1.setAvailable(true);
        item1.setOwner(requester);
        item1.setComments(Collections.emptyList());

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description of Item 2");
        item2.setAvailable(true);
        item2.setOwner(requester);
        item2.setComments(Collections.emptyList());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Sample item request");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of(item1, item2));

        return itemRequest;
    }

    private ItemRequestDto createSampleItemRequestDto() {
        UserDto requester = new UserDto(1L, "John Doe", "john.doe@example.com");

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description of Item 1");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(requester);
        itemDto1.setComments(Collections.emptyList());

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description of Item 2");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(requester);
        itemDto2.setComments(Collections.emptyList());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Sample item request");
        itemRequestDto.setRequester(requester);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(List.of(itemDto1, itemDto2));

        return itemRequestDto;
    }
}