package ru.practicum.shareit.item.itemModel;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {

        private Long id;
        @NotEmpty
        private String name;
        @NotEmpty
        private String description;
        @NotNull
        private Boolean available;
        private Long ownerId;

}
