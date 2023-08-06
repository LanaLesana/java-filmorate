package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceInterface;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class FilmController {
    @Autowired
    private final FilmServiceInterface filmService;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAllFilms();
    }

    @PostMapping(value = "/films")
    public Film add(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        Film existingFilm = filmService.getFilmById(id);
        if (existingFilm == null) {
            return ResponseEntity.notFound().build();
        }
        boolean added = filmService.addLike(id, userId);

        if (added) {
            return ResponseEntity.ok(existingFilm);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Object> removeLike(@PathVariable Integer id,
                                             @PathVariable Integer userId) {
        Film existingFilm = filmService.getFilmById(id);
        if (existingFilm == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            boolean removed = filmService.removeLike(existingFilm, userId);
            if (removed) {
                return ResponseEntity.ok(existingFilm);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ValidationException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/films/popular")
    public ResponseEntity<List<Film>> getTopTenFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        List<Film> popularFilms = filmService.getTopTenFilms(count);
        if (popularFilms != null) {
            return ResponseEntity.ok(popularFilms);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Integer id) {
        if (filmService.getFilmById(id) != null) {
            return ResponseEntity.ok(filmService.getFilmById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

