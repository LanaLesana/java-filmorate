package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface DbUserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> findAllUsers();

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(User user, Integer friendId);

    ArrayList<User> getMutualFriends(Integer userId, Integer otherUserId);

    User getUserById(Integer id);

    List<User> findAllUserFriends(Integer userId);

    void removeUser(Integer id);

    void removeAllUsers();
}
