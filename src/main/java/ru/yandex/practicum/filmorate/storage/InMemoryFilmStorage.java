package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer generatedFilmId = 1;

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        isValidFilm(film);
        film.setId(generatedFilmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        isValidFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            notFound();
        }
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
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

    @Override
    public Film getFilmById(Integer id) {
        if (films.get(id) == null) {
            throw new NotFoundException("Такого фильма нет.");
        } else {
            return films.get(id);
        }
    }

    public void notFound() {
        throw new NotFoundException("Не найдено.");
    }
}
