package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DbUserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserServiceInterface {
    public final DbUserStorage dbUserStorage;

    @Override
    public User addUser(User user) {
        isValidUser(user);
        log.info("Adding user " + user.getId());
        return dbUserStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        log.info("Updating user " + user.getId());
        return dbUserStorage.updateUser(user);
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Finding all users");
        return dbUserStorage.findAllUsers();
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        isValidId(userId);
        isValidId(friendId);
        if (!userId.equals(friendId)) {
            User user = isValidUserFromDb(userId);
            User friend = isValidUserFromDb(userId);
            if (user.getFriends() == null) {
                user.setFriends(new TreeSet<>());
            }
            if (friend.getFriends() == null) {
                friend.setFriends(new TreeSet<>());
            }
            if (user != null && friend != null) {
                dbUserStorage.addFriend(userId, friendId);
                dbUserStorage.updateUser(user);
                dbUserStorage.updateUser(friend);
                getUserById(userId).getFriendshipStatus().put(friendId, true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public boolean removeFriend(User user, Integer friendId) {
        isValidUserFromDb(user.getId());
        isValidUserFromDb(friendId);
        if (dbUserStorage.getUserById(user.getId()) != null && dbUserStorage.getUserById(friendId) != null) {
            dbUserStorage.removeFriend(user, friendId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<User> getMutualFriends(Integer userId, Integer otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        isValidUser(user);
        isValidUser(otherUser);
        return dbUserStorage.getMutualFriends(userId, otherUserId);
    }

    @Override
    public User getUserById(Integer id) {
        return dbUserStorage.getUserById(id);
    }

    @Override
    public List<User> findAllUserFriends(Integer userId) {
        isValidUserFromDb(userId);
        return dbUserStorage.findAllUserFriends(userId);
    }

    public User isValidUserFromDb(Integer id) {
        if (id != null && id < 1) {
            throw new NotFoundException("Неправильно указан id пользователя");
        }
        if (dbUserStorage.getUserById(id) != null) {
            return dbUserStorage.getUserById(id);
        } else {
            throw new ValidationException("Такого пользователя не существует.");
        }
    }

    public void isValidId(Integer id) {
        if (id <= 0 && dbUserStorage.getUserById(id) != null) {
            throw new ValidationException("Указан неправильный id.");
        }
    }

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
}
