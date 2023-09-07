package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFilmStorageTest {
    private final DbFilmStorage dbFilmStorage;
    private final DbUserStorage dbUserStorage;

    @AfterEach
    void removeAll() {
        dbFilmStorage.removeAllFilms();
    }

    @Test
    void getFilmByIdTest() {
        Film film = new Film();
        film.setName("Film number one");
        film.setDescription("This is test description");
        film.setDuration(67);
        film.setReleaseDate(LocalDate.parse("1998-12-09"));
        film.setMpa(new Mpa(1, "Mpa"));

        dbFilmStorage.addFilm(film);

        Film filmById = dbFilmStorage.getFilmById(film.getId());
        assertEquals(film, filmById);
    }

    @Test
    void addFilmTest() {
        Film film = new Film();
        film.setName("Film number one");
        film.setDescription("This is test description");
        film.setDuration(67);
        film.setReleaseDate(LocalDate.parse("1998-12-09"));
        film.setMpa(new Mpa(1, "Mpa"));

        Film newFilm = dbFilmStorage.addFilm(film);
        assertEquals(film, newFilm);
    }

    @Test
    void updateFilmTest() {
        Film film = new Film();
        film.setName("Film number one");
        film.setDescription("This is test description");
        film.setDuration(67);
        film.setReleaseDate(LocalDate.parse("1998-12-09"));
        film.setMpa(new Mpa(1, "Mpa"));

        dbFilmStorage.addFilm(film);

        Film newFilm = new Film();
        newFilm.setName("Film number two");
        newFilm.setDescription("This is test description");
        newFilm.setDuration(67);
        newFilm.setReleaseDate(LocalDate.parse("1998-12-09"));
        newFilm.setMpa(new Mpa(1, "Mpa"));
        film.setId(newFilm.getId());

        Film updatedFilm = dbFilmStorage.updateFilm(newFilm);

        assertEquals(newFilm, updatedFilm);
    }


    @Test
    void getTopTenFilmsTest() {
        Film film = new Film();
        film.setName("Film number one");
        film.setDescription("This is test description");
        film.setDuration(67);
        film.setReleaseDate(LocalDate.parse("1998-12-09"));
        film.setMpa(new Mpa(1, "Mpa"));

        dbFilmStorage.addFilm(film);

        Film newFilm = new Film();
        newFilm.setName("Film number two");
        newFilm.setDescription("This is test description");
        newFilm.setDuration(67);
        newFilm.setReleaseDate(LocalDate.parse("1998-12-09"));
        newFilm.setMpa(new Mpa(1, "Mpa"));
        film.setId(newFilm.getId());

        User user = new User();
        user.setEmail("email@mail.com");
        user.setBirthday(LocalDate.parse("1990-12-09"));
        user.setLogin("Longin");
        user.setName("Name");

        dbUserStorage.addUser(user);
        dbFilmStorage.addLike(newFilm.getId(), user.getId());
        List<Film> topTenFilms = dbFilmStorage.getTopTenFilms(10);

        assertEquals(newFilm, topTenFilms.get(0));
        assertEquals(film, topTenFilms.get(1));
    }
}
