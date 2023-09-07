package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DbFilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService implements FilmServiceInterface {
    public final DbFilmStorage dbFilmStorage;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Film film = dbFilmStorage.getFilmById(filmId);
        isValidFilm(film);
        if (userId != null) {
            log.info("Adding like to film " + film.getId() + "from user " + userId);
            if (film.getLikes() == null) {
                film.setLikes(new TreeSet<>());
                film.getLikes().add(userId);
            }
            log.info("Like has been added.");
            dbFilmStorage.addLike(filmId, userId);
        } else {
            log.info("User Id is null");
        }
    }

    @Override
    public boolean removeLike(Film film, Integer userId) {
        isValidFilm(film);
        if (userId != null && userId > 0) {
            Film existingFilm = dbFilmStorage.getFilmById(film.getId());
            if (existingFilm != null && existingFilm.getLikes() != null) {
                dbFilmStorage.removeLike(existingFilm, userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Film> getTopTenFilms(Integer count) {
        if (dbFilmStorage.getTopTenFilms(10) != null) {
            return dbFilmStorage.getTopTenFilms(10);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Film addFilm(Film film) {
        isValidFilm(film);
        log.info("Adding film " + film.getId());
        return dbFilmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Updating film " + film.getId());
        return dbFilmStorage.updateFilm(film);
    }

    @Override
    public List<Film> findAllFilms() {
        log.info("Finding all films ");
        return dbFilmStorage.findAllFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        return dbFilmStorage.getFilmById(id);
    }

    public void isValidFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Неверное название");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Описание превышает 200 символов.");
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Неверная дата релиза.");
        } else if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Указана неверная продолжительность фильма.");
        }
    }

    public Genre getGenre(int id) {
        return dbFilmStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return dbFilmStorage.getAllGenres();
    }

    public Mpa getMpa(int id) {
        return dbFilmStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return dbFilmStorage.getAllMpa();
    }
}
