package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    @AfterEach
    public void tearDown() {
        filmController = null;
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