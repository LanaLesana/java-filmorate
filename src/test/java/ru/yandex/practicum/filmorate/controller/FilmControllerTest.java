package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    void findAll() {
        List<Film> allFilmsCreated = filmController.findAll();
        assertNotNull(allFilmsCreated);
    }

    @Test
    void add() {
        Film createdFilm = filmController.add(new Film(1, "FilmName", "Description", LocalDate.of(1990, 11, 11), 2, new TreeSet<>(), new TreeSet<>(), new Mpa(1, "name"), 1));
        assertNotNull(createdFilm.getId());
        assertNotNull(createdFilm.getName());
        assertNotNull(createdFilm.getDescription());
        assertNotNull(createdFilm.getDuration());
    }

    @Test
    void update() {
        Film createdFilm = filmController.add(new Film());
        Film updatedFilm = new Film();
        filmController.update(updatedFilm);
        assertNotNull(createdFilm.getId());
        assertNotNull(createdFilm.getName());
        assertNotNull(createdFilm.getDescription());
        assertNotNull(createdFilm.getDuration());
    }
}