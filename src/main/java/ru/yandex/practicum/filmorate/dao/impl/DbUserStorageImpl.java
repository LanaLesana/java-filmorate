package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DbUserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DbUserStorageImpl implements DbUserStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        if (getUserByLogin(user.getLogin()) != null && getUserByLogin(user.getLogin()).equals(user)) {
            log.error("Попытка добавить существующего пользователя", user.getLogin());
            throw new ValidationException("Такой пользователь уже существует в базе " + user.getLogin());
        } else {
            if (user.getId() == null) {
                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("users")
                        .usingGeneratedKeyColumns("user_id");
                int id = simpleJdbcInsert.executeAndReturnKey(userToRow(user)).intValue();
                user.setId(id);
            } else {
                jdbcTemplate.update("UPDATE users SET name = ?, email = ? WHERE user_id = ?",
                        user.getName(), user.getEmail(), user.getId());
            }
            return user;
        }
    }

    private static HashMap<String, Object> userToRow(User user) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int userId = user.getId();
        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                userId
        );
        if (rowsUpdated == 1) {
            log.info("Успешно обновлено.", user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users", userRowMapper());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("INSERT INTO friend_list (user_id,friend_Id) VALUES(?,?)", userId, friendId);
    }

    @Override
    public void removeFriend(User user, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?", user.getId(), friendId);
    }

    @Override
    public ArrayList<User> getMutualFriends(Integer userId, Integer otherUserId) {
        List<Integer> user1Friends = jdbcTemplate.query(
                "SELECT friend_id FROM friend_list WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("friend_id"), userId);

        List<Integer> user2Friends = jdbcTemplate.query(
                "SELECT friend_id FROM friend_list WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("friend_id"), otherUserId);

        List<Integer> mutualFriendsIds = user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toList());
        ArrayList<User> mutualFriends = new ArrayList<>();
        for (Integer id :
                mutualFriendsIds) {
            mutualFriends.add(getUserById(id));
        }
        return mutualFriends;
    }

    @Override
    public User getUserById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
    }

    private User getUserByLogin(String login) {
        try {
            String sqlQuery = "SELECT * FROM users WHERE login = ?";
            return jdbcTemplate.queryForObject(sqlQuery, userRowMapper(), login);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> findAllUserFriends(Integer userId) {
        List<Integer> friendsId = jdbcTemplate.query(
                "SELECT friend_id FROM friend_list WHERE user_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("friend_id"), userId);
        List<User> friends = new ArrayList<>();
        for (Integer id :
                friendsId) {
            friends.add(getUserById(id));
        }
        return friends;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();

            Integer id = rs.getInt("user_id");
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("name");
            LocalDateTime birthday = rs.getTimestamp("birthday").toLocalDateTime();

            user.setId(id);
            user.setEmail(email);
            user.setLogin(login);
            user.setBirthday(birthday.toLocalDate());
            user.setName(name);

            populateFriendStatusMap(user);

            return user;
        };
    }

    public void populateFriendStatusMap(User user) {
        String sql = "SELECT friend_id FROM friend_list WHERE user_id = ?";
        HashMap<Integer, Boolean> friendStatusMap = new HashMap<>();

        jdbcTemplate.query(sql, new Object[]{user.getId()}, (rs) -> {
            while (rs.next()) {
                Integer friendId = rs.getInt("friend_id");
                boolean isMutual = checkMutualFriendship(user.getId(), friendId);
                friendStatusMap.put(friendId, isMutual);
            }
        });
        user.setFriendshipStatus(friendStatusMap);
    }

    private boolean checkMutualFriendship(Integer userId1, Integer userId2) {
        String sql = "SELECT COUNT(*) FROM friend_list WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{userId1, userId2, userId2, userId1}, Integer.class);
        return count == 2;
    }

    @Override
    public void removeUser(Integer id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ? ", id);
    }

    @Override
    public void removeAllUsers() {
        jdbcTemplate.update("DELETE FROM users");
    }
}
