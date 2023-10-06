package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface DbFilmStorage {
    void addLike(Integer filmId, Integer userId);

    void removeLike(Film film, Integer userId);

    List<Film> getTopTenFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteAllGenresFromFilm(long filmId);

    boolean addGenreToFilm(long filmId, int genreId);

    List<Film> findAllFilms();

    Film getFilmById(Integer id);

    Genre getGenreById(int id);

    List<Genre> getGenreByFilmId(int id);

    List<Genre> getAllGenres();

    Mpa getMpaById(int mpaId);

    List<Mpa> getAllMpa();

    void removeFilm(Integer id);

    void removeAllFilms();

    Film addFilmGenres(Film film);

    void removeFilmGenres(int filmId);

    Film addFilmLikes(Film film);

}
