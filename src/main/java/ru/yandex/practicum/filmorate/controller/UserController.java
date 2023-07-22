package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class UserController {
    private LinkedHashMap<Integer, User> users = new LinkedHashMap<>();
    private Integer generatedUserId = 1;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        isValidUser(user);
        user.setId(generatedUserId++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        isValidUser(user);
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    private void isValidUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Указан неправильный e-mail.");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пустой или содержит пробелы.");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Указан неправильная дата рождения.");
        }
    }
}


