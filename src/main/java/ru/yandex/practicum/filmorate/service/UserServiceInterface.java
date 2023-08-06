package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserServiceInterface {
    User addUser(User user);

    User updateUser(User user);

    List<User> findAllUsers();

    boolean addFriend(Integer userId, Integer friendId);

    boolean removeFriend(User user, Integer friendId);

    ArrayList<User> getMutualFriends(Integer userId, Integer otherUserId);

    User getUserById(Integer id);

    List<User> findAllUserFriends(Integer userId);
}
