package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @Column(name = "end_date")
    @Future
    @NotNull
    private LocalDateTime end;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booker_id", referencedColumnName = "id", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;
}