package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private TreeSet<Integer> likes;
    private List<Genre> genres = new ArrayList<>();
    private Genre genre;
    private Mpa mpa;
    private Integer mpaId;

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }

}





