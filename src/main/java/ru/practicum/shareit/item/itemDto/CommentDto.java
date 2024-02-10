package ru.practicum.shareit.item.itemDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotEmpty
    private String text;
    private String authorName;

    private LocalDateTime created;

}
