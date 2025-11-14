package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ItemRequest request1;
    private ItemRequest request2;
    private User user1;
    private User user2;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        user1 = userRepository.save(user1); // сохраняем и получаем сгенерированный ID

        user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2 = userRepository.save(user2);

        request1 = new ItemRequest();
        request1.setDescription("Нужен молоток");
        request1.setRequester(user1); // теперь user1 уже сохранен в базе
        request1.setCreated(LocalDateTime.now());
        request1 = itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Нужна дрель");
        request2.setRequester(user2);
        request2.setCreated(LocalDateTime.now());
        request2 = itemRequestRepository.save(request2);
    }

    @Test
    @DisplayName("getItemById возвращает Optional с ItemRequest")
    void getItemById_found() {
        Optional<ItemRequest> found = itemRequestRepository.getItemById(request1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Нужен молоток");
    }

    @Test
    @DisplayName("getItemById возвращает пустой Optional если не найдено")
    void getItemById_notFound() {
        Optional<ItemRequest> found = itemRequestRepository.getItemById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByRequesterIdOrderByCreatedDesc возвращает запросы пользователя")
    void findByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequester().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("findByRequesterIdNotOrderByCreatedDesc возвращает запросы других пользователей")
    void findByRequesterIdNotOrderByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequester().getName()).isEqualTo("Bob");
    }
}
