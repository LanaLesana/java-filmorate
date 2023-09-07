package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new LinkedHashMap<>();
    private Integer generatedUserId = 1;

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь не найден.");
        } else {
            return users.get(id);
        }
    }

    @Override
    public User addUser(User user) {
        isValidUser(user);
        user.setId(generatedUserId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        isValidUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            notFound();
        }
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void isValidUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Указан неправильный e-mail.");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пустой или содержит пробелы.");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Указана неправильная дата рождения.");
        }
    }

    public void notFound() {
        throw new NotFoundException("Не найдено.");
    }
}

