package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyDate;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> getItemById(Long bookingId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'"
    )
    List<Booking> checkApprovedBookingExist(Long bookerId, Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (" +
            ":timeFilter IS NULL " +
            "OR (:timeFilter = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.startDate AND b.endDate) " +
            "OR (:timeFilter = 'PAST' AND b.endDate < CURRENT_TIMESTAMP) " +
            "OR (:timeFilter = 'FUTURE' AND b.startDate > CURRENT_TIMESTAMP)" +
            ")"
    )
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                         @Param("status") BookingStatus status,
                                         @Param("timeFilter") String timeFilter);

    //ALL - Запрос всех бронирований текущего пользователя
    List<Booking> findByBookerIdOrderByStartDateDesc(Long bookerId);

    //CURRENT
    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now1,
                                                                                      LocalDateTime now2);

    //PAST
    List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    //FUTURE
    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    //WAITING
    //REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus bookingStatus);

    @Query(value = "SELECT id, start_date, end_date, status FROM bookings " +
            "WHERE start_date > NOW() and item_id=?1 " +
            "ORDER BY start_date ASC " +
            "LIMIT 1",
            nativeQuery = true
    )
    BookingDtoOnlyDate getNextBookingById(Long item_id);

    @Query(
            value = "SELECT id, start_date, end_date, status FROM bookings " +
                    "WHERE end_date < NOW() and item_id=?1 " +
                    "ORDER BY end_date DESC " +
                    "LIMIT 1",
            nativeQuery = true
    )
    BookingDtoOnlyDate getLastBookingById(Long item_id);
}
