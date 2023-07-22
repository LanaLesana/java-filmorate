package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Film {
    @NotBlank @NotEmpty @NotNull
    private Integer id;
    @NotBlank @NotEmpty @NotNull
    private String name;
    @NotBlank @NotEmpty @NotNull
    private String description;
    @NotBlank @NotEmpty @NotNull
    private LocalDate releaseDate;
    @NotBlank @NotEmpty @NotNull
    private Integer duration;
}
