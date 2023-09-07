package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void init() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void findAllUsers() {
        List<User> allUsersCreated = userController.findAllUsers();
        assertNotNull(allUsersCreated);
    }

    @Test
    void addUser() {
        User createdUser = userController.addUser(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>()));
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }

    @Test
    void update() {
        User createdUser = userController.addUser(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>()));
        User updatedUser = new User(1, "email@gmail.com", "UpdatedLogin", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>());
        userController.update(updatedUser);
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }
}