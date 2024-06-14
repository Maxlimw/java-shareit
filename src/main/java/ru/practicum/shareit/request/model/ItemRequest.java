package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    private LocalDateTime created;

    @OneToMany(mappedBy = "requestId", fetch = FetchType.LAZY)
    private List<Item> items;
}
