package ru.yandex.practicum.filmorate.controller;


import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer generatedFilmId = 1;

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
    @PostMapping(value = "/film")

    public Film add(@RequestBody Film film) {
        isValidFilm(film);
        film.setFilmId(generatedFilmId++);
        films.put(film.getFilmId(), film);
        return film;
    }
    @PutMapping(value = "/film")

    public Film update(@RequestBody Film film) {
        isValidFilm(film);
        if (films.containsKey(film.getFilmId())) {
            films.remove(film.getFilmId());
            films.put(film.getFilmId(),film);
        }
        return film;
    }
    private void isValidFilm(Film film) {
        if(film.getName() == null || film.getName().isBlank())  {
            throw new ValidationException("Неверное название");
        }else if(film.getDescription().length() > 200) {
            throw new ValidationException("Описание превышает 200 символов.");
        }else if(film.getDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Неверная дата релиза.");
        }else if(film.getDuration() < 0) {
            throw new ValidationException("Указана неверная продолжительность фильма.");
        }
    }
}

