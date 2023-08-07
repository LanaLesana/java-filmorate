package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void init() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
    }

    @Test
    void findAll() {
        List<Film> allFilmsCreated = filmController.findAll();
        assertNotNull(allFilmsCreated);
    }

    @Test
    void add() {
        Film createdFilm = filmController.add(new Film(1, "FilmName", "Description", LocalDate.of(1990, 11, 11), 2, new TreeSet<>()));
        assertNotNull(createdFilm.getId());
        assertNotNull(createdFilm.getName());
        assertNotNull(createdFilm.getDescription());
        assertNotNull(createdFilm.getDuration());
    }

    @Test
    void update() {
        Film createdFilm = filmController.add(new Film(1, "FilmName", "Description", LocalDate.of(1990, 11, 11), 2, new TreeSet<>()));
        Film updatedFilm = new Film(1, "FilmNameUpdated", "Description", LocalDate.of(1990, 11, 11), 2, new TreeSet<>());
        filmController.update(updatedFilm);
        assertNotNull(createdFilm.getId());
        assertNotNull(createdFilm.getName());
        assertNotNull(createdFilm.getDescription());
        assertNotNull(createdFilm.getDuration());
    }
}