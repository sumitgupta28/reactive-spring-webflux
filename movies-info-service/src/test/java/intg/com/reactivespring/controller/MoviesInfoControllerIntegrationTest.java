package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    String BASE_URL = "/v1/movieinfos";

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
    void addMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(movieInfo).exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoReturn != null;
                    assert movieInfoReturn.getMovieInfoId() != null;
                });
    }


    @Test
    void addMovieInfoWithId() {

        var movieInfo = new MovieInfo(null, "Batman Begins2",
                2005, List.of("Christian Bale-1", "Michael Cane-1"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(movieInfo).exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoReturn != null;
                    assert movieInfoReturn.getMovieInfoId() != null;

                    webTestClient.get()
                            .uri(BASE_URL + "/{id}", movieInfoReturn.getMovieInfoId())
                            .exchange()
                            .expectStatus()
                            .is2xxSuccessful()
                            .expectBody(MovieInfo.class)
                            .consumeWith(movieInfoEntityExchangeResult1 ->
                            {
                                var movieInfoReturnVal = movieInfoEntityExchangeResult.getResponseBody();
                                assert movieInfoReturnVal != null;
                                assert movieInfoReturnVal.getMovieInfoId() != null;
                            });
                });


    }

    @Test
    void getAllMovie() {
        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieById() {
        var movieInfoId = "abc";

        webTestClient.get()
                .uri(BASE_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoReturn != null;
                });

    }

    @Test
    void deleteMovieById() {
        var movieInfoId = "abc";

        webTestClient.delete()
                .uri(BASE_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();


        webTestClient.get()
                .uri(BASE_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    void updatedMovieInfo() {

        var movieInfoId = "abc";

        var updatedMovieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri(BASE_URL + "/{id}", movieInfoId)
                .bodyValue(updatedMovieInfo).exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoReturn != null;
                    assert movieInfoReturn.getMovieInfoId() != null;

                    assertEquals(movieInfoReturn.getName(), updatedMovieInfo.getName());
                    assertEquals(movieInfoReturn.getCast(), updatedMovieInfo.getCast());
                    assertEquals(movieInfoReturn.getYear(), updatedMovieInfo.getYear());
                    assertEquals(movieInfoReturn.getRelease_date(), updatedMovieInfo.getRelease_date());
                });


        webTestClient.get()
                .uri(BASE_URL + "/{id}", movieInfoId).exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoReturn != null;
                    assert movieInfoReturn.getMovieInfoId().equals(movieInfoId);
                    assert movieInfoReturn.getName().equalsIgnoreCase(updatedMovieInfo.getName());
                    assert movieInfoReturn.getCast().equals(updatedMovieInfo.getCast());
                    assert movieInfoReturn.getYear().equals(updatedMovieInfo.getYear());
                    assert movieInfoReturn.getRelease_date().equals(updatedMovieInfo.getRelease_date());

                });
    }
}