package ru.practicum.shareit.item.itemService;

import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, Long userId) throws UserNotFoundException;

    ItemDto editItem(Item item, Long itemId, Long userId) throws ItemNotFoundException, UserNotOwnerException;

    ItemDto getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String text);

}