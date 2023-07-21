package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    private Integer generatedUserId = 1;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/user")
        public User addUser(@RequestBody User user) {
        isValidUser(user);
            user.setUserId(generatedUserId++);
            users.put(user.getUserId(), user);
            return user;
    }
    @PutMapping(value = "/user")
    public User update(@RequestBody User user) {
        isValidUser(user);
       if (users.containsKey(user.getUserId())) {
            users.remove(user.getUserId());
            users.put(user.getUserId(),user);
        }
        return user;
    }
    private void isValidUser(User user) {
        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))  {
            throw new ValidationException("Указан неправильный e-mail.");
        }else if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пустой или содержит пробелы.");
        }else if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }else if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Указан неправильная дата рождения.");
        }
    }
}


