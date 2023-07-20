package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Film {
    private Integer filmId;
    private String name;
    private String description;
    private LocalDate date;
    private int duration;
}
