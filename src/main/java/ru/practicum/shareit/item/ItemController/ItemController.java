package ru.practicum.shareit.item.ItemController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.item.itemService.ItemService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.addItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@RequestBody Item item,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId)  {
        return ResponseEntity.ok().body(itemService.editItem(item, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok().body(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        return ResponseEntity.ok().body(itemService.search(text));
    }
}