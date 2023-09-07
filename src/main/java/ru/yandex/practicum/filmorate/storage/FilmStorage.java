package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> findAllFilms();

    void isValidFilm(Film film);

    Film getFilmById(Integer id);
}
