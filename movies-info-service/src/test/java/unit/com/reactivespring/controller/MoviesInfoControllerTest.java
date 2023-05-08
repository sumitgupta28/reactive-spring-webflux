package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {

    private final String BASE_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo("MovieInfoId", "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.addMovie(Mockito.any(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

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
    void addMovieInfo_Validation() {

        var movieInfo = new MovieInfo("MovieInfoId", null,
                null, List.of("", "Michael Cane"), LocalDate.parse("2005-06-15"));

        // Mockito.when(movieInfoService.addMovie(Mockito.any(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(movieInfo).exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    var expectedErrorMessage = "movieInfo.cast must be  present ,movieInfo.name must be  present ,movieInfo.name must be a positive number";
                    assertEquals(expectedErrorMessage, responseBody);
                });
    }

    @Test
    void getMovieInfoById() {

        MovieInfo movieInfo =
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.getMovieById(Mockito.anyString())).thenReturn(Mono.just(movieInfo));

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
    void delete() {

        var movieInfoId = "abc";
        Mockito.when(movieInfoService.deleteMovieById(Mockito.anyString())).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getAllMovie() {

        var moviesList = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        Mockito.when(movieInfoService.getAllMovies()).thenReturn(Flux.fromIterable(moviesList));

        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }


    @Test
    void updatedMovieInfo() {

        var movieInfoId = "MockId";

        var updatedMovieInfo = new MovieInfo(movieInfoId, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.updateMovieInfo(ArgumentMatchers.isA(MovieInfo.class),
                Mockito.anyString())).thenReturn(Mono.just(updatedMovieInfo));


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


    }
}