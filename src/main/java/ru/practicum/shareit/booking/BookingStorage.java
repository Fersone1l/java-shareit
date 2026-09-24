package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Booking.Status status, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start < :now and b.end > :now")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.end < :now")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.start > :now")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                         @Param("status") Booking.Status status, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id in :itemIds and b.status = 'APPROVED' " +
            "order by b.start asc")
    List<Booking> findAllApprovedForItems(@Param("itemIds") List<Long> itemIds);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.booker.id = :userId " +
            "and b.status = 'APPROVED' " +
            "and b.end < :now")
    List<Booking> findCompletedBookings(@Param("itemId") Long itemId,
                                        @Param("userId") Long userId,
                                        @Param("now") LocalDateTime now);
}