package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    public Map<Integer, User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    List<User> findAllUsers();

    void isValidUser(User user);

}
