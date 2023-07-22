package ru.yandex.practicum.filmorate.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor

public class User {
    @NotBlank @NotEmpty @NotNull
    private Integer id;
    @Email @NotBlank @NotEmpty @NotNull
    private String email;
    @NotBlank @NotEmpty @NotNull
    private String login;
    private String name;
    @NotBlank @NotEmpty @NotNull
    private LocalDate birthday;
}
