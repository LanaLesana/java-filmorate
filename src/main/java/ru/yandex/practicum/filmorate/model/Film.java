package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private TreeSet<Integer> likes;
}
