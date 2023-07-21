package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @AfterEach
    public void tearDown() {
        userController = null;
    }

    @Test
    void findAllUsers() {
        List<User> allUsersCreated = userController.findAllUsers();
        assertNotNull(allUsersCreated);
    }

    @Test
    void addUser() {
        User createdUser = userController.addUser(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11)));
        assertNotNull(createdUser.getUserId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }

    @Test
    void update() {
        User createdUser = userController.update(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11)));
        assertNotNull(createdUser.getUserId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }
}