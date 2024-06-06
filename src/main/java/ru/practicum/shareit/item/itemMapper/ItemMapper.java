package ru.practicum.shareit.item.itemMapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;


@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);
}
