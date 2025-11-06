package ru.practicum.shareit.request.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest("spring.profiles.active=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    private final EntityManager em;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    User user1 = new User();
    User user2 = new User();

    ItemRequest request1 = new ItemRequest();
    ItemRequest request2 = new ItemRequest();
    ItemRequest request3 = new ItemRequest();
    ItemRequest request4 = new ItemRequest();

    @BeforeEach
    public void createData() {
        user1.setName("Test Name1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        user2.setName("Test Name2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        request1.setDescription("Description1");
        request1.setCreated(LocalDateTime.now().minusHours(1));
        request1.setRequester(user1);
        itemRequestRepository.save(request1);

        request2.setDescription("Description2");
        request2.setCreated(LocalDateTime.now().minusHours(2));
        request2.setRequester(user1);
        itemRequestRepository.save(request2);

        request3.setDescription("Description3");
        request3.setCreated(LocalDateTime.now().minusHours(3));
        request3.setRequester(user2);
        itemRequestRepository.save(request3);

        request4.setDescription("Description4");
        request4.setCreated(LocalDateTime.now().minusHours(4));
        request4.setRequester(user2);
        itemRequestRepository.save(request4);

    }

    @AfterEach
    public void cleanup() {
        itemRequestRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void getItemByIdTest() {
        TypedQuery<ItemRequest> query = em.createQuery("SELECT r FROM ItemRequest r WHERE r.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", 1L)
                .getSingleResult();
        assertThat(itemRequest.getId(), equalTo(1L));
        assertThat(itemRequest.getDescription(), equalTo("Description1"));
        assertEquals(LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS), itemRequest.getCreated().truncatedTo(ChronoUnit.SECONDS),
                "Время создания запроса отличается");
        assertThat(itemRequest.getRequester().getId(), equalTo(user1.getId()));
    }

    @Test
    void findByRequesterIdOrderByCreatedDescTest() {
        long requesterId = user1.getId();
        List<ItemRequest> results = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        assertNotNull(results);
        assertEquals(2, results.size()); // Должны вернуть только две записи, принадлежащие первому пользователю
        assertEquals(request1.getId(), results.get(0).getId()); // Сначала идет самая новая запись
        assertEquals(request2.getId(), results.get(1).getId()); // Потом старая запись
    }

    @Test
    void findByRequesterIdNotOrderByCreatedDescTest() {
        long requesterId = user1.getId();
        List<ItemRequest> results = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(requesterId);
        assertNotNull(results);
        assertEquals(2, results.size()); // Должны вернуть только две записи, принадлежащие первому пользователю
        assertEquals(request3.getId(), results.get(0).getId()); // Сначала идет самая новая запись
        assertEquals(request4.getId(), results.get(1).getId()); // Потом старая запись
    }
}