package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest // создаёт контекст только для JPA
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Сохранение и поиск пользователя по ")
    void getUserByIdTest() {
        // given
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        Long id = userRepository.save(user).getId();
        // when
        Optional<User> found = userRepository.getUserById(id);
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void uniqueEmailConstraintTest() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("alice@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Сохранение и поиск пользователя по email")
    void findAllByEmailTest() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        userRepository.save(user1);

        Optional<User> found = userRepository.findAllByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }
}