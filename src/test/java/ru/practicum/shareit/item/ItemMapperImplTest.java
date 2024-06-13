package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.itemDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemMapper.CommentMapper;
import ru.practicum.shareit.item.itemMapper.ItemMapperImpl;
import ru.practicum.shareit.item.itemModel.Comment;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ItemMapperImplTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemMapperImpl itemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toItemDto_shouldMapItemToItemDto() {
        Item item = createSampleItem();

        when(commentMapper.toCommentDto(item.getComments().get(0))).thenReturn(new CommentDto(1L, "Sample comment", "John Doe", LocalDateTime.now()));

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequestId(), itemDto.getRequestId());
        assertEquals(item.getOwner().getId(), itemDto.getOwner().getId());
        assertEquals(item.getOwner().getName(), itemDto.getOwner().getName());
        assertEquals(item.getOwner().getEmail(), itemDto.getOwner().getEmail());

        assertEquals(1, itemDto.getComments().size());
        assertEquals("Sample comment", itemDto.getComments().get(0).getText());
        assertEquals("John Doe", itemDto.getComments().get(0).getAuthorName());
    }

    @Test
    void toItem_shouldMapItemDtoToItem() {
        ItemDto itemDto = createSampleItemDto();

        when(commentMapper.toComment(itemDto.getComments().get(0))).thenReturn(new Comment(1L, "Sample comment", null, null, LocalDateTime.now()));

        Item item = itemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
        assertEquals(itemDto.getOwner().getId(), item.getOwner().getId());
        assertEquals(itemDto.getOwner().getName(), item.getOwner().getName());
        assertEquals(itemDto.getOwner().getEmail(), item.getOwner().getEmail());

        assertEquals(1, item.getComments().size());
        assertEquals("Sample comment", item.getComments().get(0).getText());
    }

    private Item createSampleItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("John Doe");
        owner.setEmail("john.doe@example.com");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Sample comment");
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("Sample Item");
        item.setDescription("Description of Sample Item");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(1L);
        item.setComments(Collections.singletonList(comment));

        return item;
    }

    private ItemDto createSampleItemDto() {
        UserDto owner = new UserDto();
        owner.setId(1L);
        owner.setName("John Doe");
        owner.setEmail("john.doe@example.com");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Sample comment");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.now());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Sample Item");
        itemDto.setDescription("Description of Sample Item");
        itemDto.setAvailable(true);
        itemDto.setOwner(owner);
        itemDto.setRequestId(1L);
        itemDto.setComments(Collections.singletonList(commentDto));

        return itemDto;
    }
}