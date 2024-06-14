package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional(readOnly = true)
    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    @Transactional(readOnly = true)
    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                              Pageable pageable);

    @Transactional(readOnly = true)
    Page<Booking> findByBookerIdAndStatusAndEndIsBefore(Long bookerId, Status status, LocalDateTime end,
                                                        Pageable pageable);

    @Transactional(readOnly = true)
    Page<Booking> findByBookerIdAndStatusInAndStartIsAfter(Long bookerId, List<Status> statuses, LocalDateTime start,
                                                           Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.id in (" +
            "      select i.id from Item i" +
            "       where i.owner.id = ?1) " +
            "order by b.start desc")
    @Transactional(readOnly = true)
    Page<Booking> findAllBookingsByOwner(Long ownerIds, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.id in (" +
            "      select i.id from Item i" +
            "       where i.owner.id = ?1) " +
            "  and b.status in ?2 " +
            " order by b.start desc")
    @Transactional(readOnly = true)
    Page<Booking> findAllBookingsByOwner(Long ownerId, List<Status> statuses, Pageable pageable);

    @Transactional(readOnly = true)
    List<Booking> findAllBookingsByItemId(Long itemId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "  and b.item.id = ?2 " +
            "  and b.status = 'APPROVED' " +
            "  and b.start < ?3")
    @Transactional(readOnly = true)
    List<Booking> findPastAndCurrentActiveBookingsByBookerIdAndItemId(Long userId, Long itemId, LocalDateTime dateTime);
}