package ru.practicum.shareit.item.ItemController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.itemDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemMapper.ItemMapper;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.item.itemService.ItemService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.toItem(itemDto);
        return ResponseEntity.ok().body(itemService.addItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@RequestBody ItemDto itemDto,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId)  {
        Item item = itemMapper.toItem(itemDto);
        return ResponseEntity.ok().body(itemService.editItem(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemId") Long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        return ResponseEntity.ok().body(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto,
                                                 @PathVariable("itemId") Long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.addComment(commentDto, itemId, userId));
    }
}