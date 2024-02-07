package ru.practicum.shareit.item.itemDto;

/**
 * TODO Sprint add-controllers.
 */
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long ownerId;

}
