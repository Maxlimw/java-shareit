package ru.practicum.shareit.item.itemService;

import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, Long userId);

    ItemDto editItem(Item item, Long itemId, Long userId);

    ItemDto getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String text);

}