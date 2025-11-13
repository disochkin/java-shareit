package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("checkApprovedBookingExist — возвращает только утверждённые бронирования по пользователю и вещи")
    void checkApprovedBookingExist_ShouldReturnApprovedBookings() {
        // given
        User booker = new User();
        booker.setName("Alice");
        booker.setEmail("alice@example.com");
        em.persist(booker);

        User owner = new User();
        owner.setName("Bob");
        owner.setEmail("bob@example.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking approvedBooking = new Booking();
        approvedBooking.setBooker(booker);
        approvedBooking.setItem(item);
        approvedBooking.setStartDate(LocalDateTime.now().plusHours(1));
        approvedBooking.setEndDate(LocalDateTime.now().plusHours(2));
        approvedBooking.setStatus(BookingStatus.APPROVED);
        em.persist(approvedBooking);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setBooker(booker);
        rejectedBooking.setItem(item);
        rejectedBooking.setStartDate(LocalDateTime.now().plusHours(1));
        rejectedBooking.setEndDate(LocalDateTime.now().plusHours(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        em.persist(rejectedBooking);

        em.flush();
        em.clear();

        // when
        List<Booking> result = bookingRepository.checkApprovedBookingExist(booker.getId(), item.getId());

        // then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(b -> {
                    assertThat(b.getStatus()).isEqualTo(BookingStatus.APPROVED);
                    assertThat(b.getBooker().getId()).isEqualTo(booker.getId());
                    assertThat(b.getItem().getId()).isEqualTo(item.getId());
                });
    }

    @Test
    @DisplayName("checkApprovedBookingExist — возвращает пустой список, если нет APPROVED-бронирований")
    void checkApprovedBookingExist_ShouldReturnEmptyList_WhenNoApproved() {
        // given
        User booker = new User();
        booker.setName("Charlie");
        booker.setEmail("charlie@example.com");
        em.persist(booker);

        User owner = new User();
        owner.setName("Dan");
        owner.setEmail("dan@example.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Hammer");
        item.setDescription("Heavy hammer");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking waitingBooking = new Booking();
        waitingBooking.setBooker(booker);
        waitingBooking.setItem(item);
        waitingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        waitingBooking.setEndDate(LocalDateTime.now().plusHours(2));
        waitingBooking.setStatus(BookingStatus.WAITING);
        em.persist(waitingBooking);

        em.flush();

        // when
        List<Booking> result = bookingRepository.checkApprovedBookingExist(booker.getId(), item.getId());

        // then
        assertThat(result).isEmpty();
    }}