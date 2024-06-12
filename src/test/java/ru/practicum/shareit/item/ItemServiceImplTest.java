package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.item.itemService.ItemService;
import ru.practicum.shareit.item.itemService.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void shouldExceptionWhenEditNotExistingItem() {
        ItemService itemService = new ItemServiceImpl(mockUserRepository, mockItemRepository, null,
                null, null, null, null);

        when(mockUserRepository.existsById(any(Long.class)))
                .thenReturn(true);

        when(mockItemRepository.existsById(any(Long.class)))
                .thenReturn(false);

        ItemDto itemDto = new ItemDto(1L, "item1", "description1", true, null, null);

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.editItem(itemDto, itemDto.getId(), 2L));
        assertEquals(String.format("Вещь с id = %d не найдена!", itemDto.getId()), exception.getMessage());
    }
}