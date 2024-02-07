package ru.practicum.shareit.item.itemModel;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
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
