package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceInterface;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserServiceInterface userService;


    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Object> addFriend(@PathVariable Integer id,
                                            @PathVariable Integer friendId) {
        try {
            boolean added = userService.addFriend(id, friendId);

            if (added) {
                return ResponseEntity.ok(userService.getUserById(id));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (ValidationException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable Integer id,
                                             @PathVariable Integer friendId) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        boolean removed = userService.removeFriend(existingUser, friendId);
        if (removed) {
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        List<User> mutualFriends = userService.getMutualFriends(id, otherId);
        return ResponseEntity.ok(mutualFriends);
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<List<User>> findAllUserFriends(@PathVariable Integer id) {
        if (userService.findAllUserFriends(id) != null) {
            return ResponseEntity.ok(userService.findAllUserFriends(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        if (userService.getUserById(id) != null) {
            return ResponseEntity.ok(userService.getUserById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}




