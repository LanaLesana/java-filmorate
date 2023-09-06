package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbUserStorageTest {

    private final DbUserStorage bdUserStorage;

    @AfterEach
    void removeAll() {
        bdUserStorage.removeAllUsers();
    }
    @Test
    void getUserByIdTest() {
        User user = new User();
        user.setName("Name one");
        user.setEmail("email@mail.com");
        user.setLogin("Loginone");
        user.setBirthday(LocalDate.parse("1999-09-09"));

        User addedUser = bdUserStorage.addUser(user);

        User userById = bdUserStorage.getUserById(addedUser.getId());
        assertEquals(user, userById);
    }

    @Test
    void findAllUsersTest() {
        User user = new User();
        user.setName("Name one");
        user.setEmail("email@mail.com");
        user.setLogin("Loginone");
        user.setBirthday(LocalDate.parse("1999-09-09"));

        bdUserStorage.addUser(user);

        User user2 = new User();
        user.setName("Name two");
        user.setEmail("email2@mail.com");
        user.setLogin("Loginonetwo");
        user.setBirthday(LocalDate.parse("2000-01-02"));

        bdUserStorage.addUser(user2);

        List<User> users = bdUserStorage.findAllUsers();
        assertEquals(2, users.size());
        assertEquals(user, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    void addUserTest() {
        User user = new User();
        user.setName("Name one");
        user.setEmail("email@mail.com");
        user.setLogin("Loginone");
        user.setBirthday(LocalDate.parse("1999-09-09"));


        User userToCompare = bdUserStorage.addUser(user);

        assertEquals(user, userToCompare);
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setName("Name one");
        user.setEmail("email@mail.com");
        user.setLogin("Loginone");
        user.setBirthday(LocalDate.parse("1999-09-09"));

        bdUserStorage.addUser(user);

        User user2 = new User();
        user.setName("Name two");
        user.setEmail("email2@mail.com");
        user.setLogin("Loginonetwo");
        user.setBirthday(LocalDate.parse("2000-01-02"));

        user.setId(user.getId());
        User updatedUser = bdUserStorage.updateUser(user2);

        assertEquals(user2, updatedUser);
    }
}