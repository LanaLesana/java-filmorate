package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserServiceInterface {
    public final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        log.info("Adding user " + user.getId());
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        log.info("Updating user " + user.getId());
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Finding all users");
        return userStorage.findAllUsers();
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        isValidId(userId);
        isValidId(friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.isValidUser(user);
        userStorage.isValidUser(friend);
        if (user.getFriends() == null) {
            user.setFriends(new TreeSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new TreeSet<>());
        }
        if (userStorage.getUsers().containsValue(user) && userStorage.getUserById(friendId) != null) {
            userStorage.getUserById(userId).getFriends().add(friendId);
            userStorage.getUserById(friendId).getFriends().add(user.getId());
            userStorage.updateUser(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeFriend(User user, Integer friendId) {
        userStorage.isValidUser(user);
        if (userStorage.getUsers().containsValue(user) && userStorage.getUsers().get(friendId) != null) {
            userStorage.getUsers().get(user.getId()).getFriends().remove(friendId);
            userStorage.getUsers().get(friendId).getFriends().remove(user.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<User> getMutualFriends(Integer userId, Integer otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        userStorage.isValidUser(user);
        userStorage.isValidUser(otherUser);
        TreeSet<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        TreeSet<Integer> otherUserFriends = userStorage.getUserById(otherUserId).getFriends();

        ArrayList<User> mutualFriendsList = new ArrayList<>();
        if (userFriends != null && otherUserFriends != null) {
            TreeSet<Integer> mutualFriends = new TreeSet<>(userFriends);
            mutualFriends.retainAll(otherUserFriends);

            for (int friendId : mutualFriends) {
                User mutualFriend = userStorage.getUserById(friendId);
                if (mutualFriend != null) {
                    mutualFriendsList.add(mutualFriend);
                }
            }
            return mutualFriendsList;
        } else {
            return mutualFriendsList;
        }

    }

    @Override
    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> findAllUserFriends(Integer userId) {
        User user = getUserById(userId);
        ArrayList<User> listOfFriends = new ArrayList<>();
        ArrayList<Integer> listOfFriendsIds = new ArrayList<>(user.getFriends());
        for (Integer id : listOfFriendsIds) {
            User friend = getUserById(id);
            listOfFriends.add(friend);
        }
        return listOfFriends;
    }

    public void isValidId(Integer id) {
        if (id <= 0 && userStorage.getUserById(id) != null && userStorage.getUsers().containsKey(id)) {
            throw new ValidationException("Указан неправильный id.");
        }
    }
}
