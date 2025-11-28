package ru.practicum.shareit.booking.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback
class BookingRepositoryFindByOwnerIdAndStatusTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        em.persist(user);
        return user;
    }

    private Item createItem(String name, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Some description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);
        return item;
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setStatus(status);
        em.persist(booking);
        return booking;
    }

    @Test
    @DisplayName("findByOwnerIdAndStatus — возвращает текущие бронирования владельца")
    void findByOwnerIdAndStatus_ShouldReturnCurrentBookings() {
        // given
        User owner = createUser("Owner", "owner@mail.com");
        User booker = createUser("Booker", "booker@mail.com");
        Item item = createItem("Drill", owner);

        // бронирование активно сейчас
        createBooking(booker, item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2),
                BookingStatus.APPROVED);

        // уже завершено
        createBooking(booker, item,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        em.flush();
        em.clear();

        // when
        List<Booking> result = bookingRepository.findByOwnerIdAndStatus(owner.getId(), BookingStatus.APPROVED, "CURRENT");

        // then
        assertThat(result)
                .hasSize(1)
                .allSatisfy(b -> {
                    assertThat(b.getItem().getOwner().getId()).isEqualTo(owner.getId());
                    assertThat(b.getStatus()).isEqualTo(BookingStatus.APPROVED);
                    assertThat(LocalDateTime.now()).isBetween(b.getStartDate(), b.getEndDate());
                });
    }

    @Test
    @DisplayName("findByOwnerIdAndStatus — возвращает прошедшие бронирования")
    void findByOwnerIdAndStatus_ShouldReturnPastBookings() {
        // given
        User owner = createUser("Owner", "owner@mail.com");
        User booker = createUser("Booker", "booker@mail.com");
        Item item = createItem("Hammer", owner);

        createBooking(booker, item,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED);

        createBooking(booker, item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED);

        em.flush();
        em.clear();

        // when
        List<Booking> result = bookingRepository.findByOwnerIdAndStatus(owner.getId(), BookingStatus.APPROVED, "PAST");

        // then
        assertThat(result)
                .hasSize(1)
                .allSatisfy(b -> assertThat(b.getEndDate()).isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("findByOwnerIdAndStatus — возвращает будущие бронирования")
    void findByOwnerIdAndStatus_ShouldReturnFutureBookings() {
        // given
        User owner = createUser("Owner", "owner@mail.com");
        User booker = createUser("Booker", "booker@mail.com");
        Item item = createItem("Saw", owner);

        createBooking(booker, item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                BookingStatus.WAITING);

        em.flush();
        em.clear();

        // when
        List<Booking> result = bookingRepository.findByOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING, "FUTURE");

        // then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(b -> {
                    assertThat(b.getStatus()).isEqualTo(BookingStatus.WAITING);
                    assertThat(b.getStartDate()).isAfter(LocalDateTime.now());
                });
    }

    @Test
    @DisplayName("findByOwnerIdAndStatus — возвращает все бронирования без фильтров (status=null, timeFilter=null)")
    void findByOwnerIdAndStatus_ShouldReturnAll_WhenNoFilters() {
        // given
        User owner = createUser("Owner", "owner@mail.com");
        User booker = createUser("Booker", "booker@mail.com");
        Item item = createItem("Laptop", owner);

        createBooking(booker, item,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED);

        createBooking(booker, item,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                BookingStatus.WAITING);

        em.flush();
        em.clear();

        // when
        List<Booking> result = bookingRepository.findByOwnerIdAndStatus(owner.getId(), null, null);

        // then
        assertThat(result).hasSize(2);
    }
}