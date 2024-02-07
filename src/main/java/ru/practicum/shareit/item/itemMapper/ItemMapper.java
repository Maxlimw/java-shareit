package ru.practicum.shareit.item.itemMapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toItemDto(Item item);

}
