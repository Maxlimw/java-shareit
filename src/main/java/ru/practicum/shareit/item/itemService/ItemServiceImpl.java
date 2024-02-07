package ru.practicum.shareit.item.itemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.itemDao.ItemDao;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemMapper.ItemMapper;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto addItem(Item item, Long userId) {
        if (!userService.existsById(userId)) {
            String errorMessage = String.format("Пользовать с id = %d не найден!", userId);
            log.warn(errorMessage);
            throw new UserNotFoundException(String.format(errorMessage));
        }

        item.setOwnerId(userId);

        return itemMapper.toItemDto(itemDao.save(item));
    }

    @Override
    public ItemDto editItem(Item item, Long itemId, Long userId) {
        if (!existsById(itemId)) {
            String errorMessage = String.format("Вещь с id = %d не найдена!", itemId);
            log.warn(errorMessage);
            throw new ItemNotFoundException(errorMessage);
        }

        if (!userId.equals(itemDao.get(itemId).getOwnerId())) {
            String errorMessage = String.format("Пользователь c id = %d не является владельцем вещи с id = %d!", userId,
                    itemId);
            log.warn(errorMessage);
            throw new UserNotOwnerException(errorMessage);
        }

        return itemMapper.toItemDto(itemDao.update(item, itemId));
    }

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(itemDao.get(id));
    }

    @Override
    public List<ItemDto> getItems(Long userId)  {
        return itemDao.getAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemDao.search(text.toLowerCase()).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    public boolean existsById(Long id) {
        return itemDao.getIdsList().contains(id);
    }
}
