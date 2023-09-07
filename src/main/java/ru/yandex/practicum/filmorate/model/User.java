package ru.yandex.practicum.filmorate.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.TreeSet;

@Data
@AllArgsConstructor

public class User {
    private Integer id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private TreeSet<Integer> friends;
}
