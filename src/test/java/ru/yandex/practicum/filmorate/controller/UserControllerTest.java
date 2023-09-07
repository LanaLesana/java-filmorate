package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    @Autowired
    UserController userController;

    @Test
    void findAllUsers() {
        List<User> allUsersCreated = userController.findAllUsers();
        assertNotNull(allUsersCreated);
    }

    @Test
    void addUser() {
        User createdUser = userController.addUser(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>(), new HashMap<Integer, Boolean>()));
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }

    @Test
    void update() {
        User createdUser = userController.addUser(new User(1, "email@gmail.com", "Login", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>(), new HashMap<Integer, Boolean>()));
        User updatedUser = new User(1, "email@gmail.com", "UpdatedLogin", "Name", LocalDate.of(2000, 11, 11), new TreeSet<>(), new HashMap<Integer, Boolean>());
        userController.update(updatedUser);
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getLogin());
        assertNotNull(createdUser.getName());
        assertNotNull(createdUser.getBirthday());
        assertNotNull(createdUser.getEmail());
    }
}