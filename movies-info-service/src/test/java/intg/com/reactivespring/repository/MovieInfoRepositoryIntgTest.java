package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {

        var moviesList = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(moviesList).log()
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        var movieInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void findById() {
        var movieInfoMono = movieInfoRepository.findById("abc").log();
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> assertEquals("Dark Knight Rises", movieInfo.getName())).verifyComplete();
    }


    @Test
    void findByYear() {
        var movieInfoMono = movieInfoRepository.findByYear(2005).log();
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> assertEquals("Batman Begins", movieInfo.getName())).verifyComplete();
    }


    @Test
    void findByName() {
        var movieInfoMono = movieInfoRepository.findByName("Batman Begins").log();
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getName());
                }).verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        var movieInfoToSave = movieInfoRepository.findById("abc").block();
        movieInfoToSave.setYear(2021);

        var movieInfoMono = movieInfoRepository.save(movieInfoToSave);
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(2021, movieInfo.getYear());
                }).verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        movieInfoRepository.deleteById("abc").log().block();

        var movieInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }


    @Test
    void saveMovieInfo() {
        var movieInfoToSave = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var movieInfoMono = movieInfoRepository.save(movieInfoToSave);
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Batman Begins1", movieInfo.getName());
                }).verifyComplete();
    }
}