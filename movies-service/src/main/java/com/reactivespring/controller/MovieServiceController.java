package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.MoviesReviewRestClient;
import com.reactivespring.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/v1/movies")
public class MovieServiceController {

    private final MoviesInfoRestClient moviesInfoRestClient;

    private final MoviesReviewRestClient moviesReviewRestClient;

    public MovieServiceController(MoviesInfoRestClient moviesInfoRestClient, MoviesReviewRestClient moviesReviewRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.moviesReviewRestClient = moviesReviewRestClient;
    }

    @GetMapping("/{id}")
    Mono<Movie> getMovieById(@PathVariable("id") String movieId) {
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewListMono = moviesReviewRestClient.retrieveReviews(movieId).collectList();
                    return reviewListMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }
}
