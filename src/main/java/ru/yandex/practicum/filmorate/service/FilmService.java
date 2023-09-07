package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService implements FilmServiceInterface {
    public final FilmStorage filmStorage;

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.isValidFilm(film);
        if (film.getLikes() == null) {
            film.setLikes(new TreeSet<>());
        }
        if (userId != null) {
            filmStorage.getFilms().get(filmId).getLikes().add(userId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeLike(Film film, Integer userId) {
        filmStorage.isValidFilm(film);
        if (userId != null && userId > 0) {
            Film existingFilm = filmStorage.getFilmById(film.getId());
            if (existingFilm != null && existingFilm.getLikes() != null) {
                existingFilm.getLikes().remove(userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Film> getTopTenFilms(Integer count) {
        if (filmStorage.getFilms() != null) {
            Comparator<Film> likeComparator = Comparator.comparingInt(film -> {
                if (film.getLikes() != null) {
                    return -film.getLikes().size();
                } else {
                    return 0;
                }
            });

            List<Film> films = new ArrayList<>(filmStorage.getFilms().values());
            films.sort(likeComparator);
            return films.subList(0, Math.min(count, films.size()));
        }
        return new ArrayList<>();
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Adding film " + film.getId());
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Updating film " + film.getId());
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> findAllFilms() {
        log.info("Finding all films ");
        return filmStorage.findAllFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }
}
