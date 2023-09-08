package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private Integer id;
    @Email
    @NotNull(message = "Не указан логин email")
    private String email;
    @NotNull(message = "Не указан логин")
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    private LocalDate birthday;
    private TreeSet<Integer> friends;
    private HashMap<Integer, Boolean> friendshipStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(login, user.login) &&
                Objects.equals(name, user.name) &&
                Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, login, name, birthday);
    }
}
