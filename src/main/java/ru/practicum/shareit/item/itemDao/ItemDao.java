package ru.practicum.shareit.item.itemDao;

import ru.practicum.shareit.item.itemModel.Item;
import java.util.List;

public interface ItemDao {
    Item save(Item item);

    Item update(Item item, Long itemId);

    Item get(Long itemId);

    List<Item> getAllByUserId(Long userId);

    List<Item> search(String text);

    List<Long> getIdsList();
}