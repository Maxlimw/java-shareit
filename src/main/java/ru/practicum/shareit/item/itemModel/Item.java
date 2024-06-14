package ru.practicum.shareit.item.itemModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;


        private String description;

        @Column(name = "is_available")
        private Boolean available;

        @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "owner_id", nullable = false)
        private User owner;

        @Column(name = "request_id")
        private Long requestId;

        @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Comment> comments;

}
