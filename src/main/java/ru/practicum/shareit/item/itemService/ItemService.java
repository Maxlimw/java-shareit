package ru.practicum.shareit.item.itemService;

import ru.practicum.shareit.item.itemDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto editItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(CommentDto comment, Long itemId, Long userId);
}