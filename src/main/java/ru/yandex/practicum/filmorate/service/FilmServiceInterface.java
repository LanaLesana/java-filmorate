package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInterface {
    boolean addLike(Integer filmId, Integer userId);

    boolean removeLike(Film film, Integer userId);

    List<Film> getTopTenFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> findAllFilms();

    Film getFilmById(Integer id);
}
