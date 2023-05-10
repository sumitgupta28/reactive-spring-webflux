package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class MoviesReviewRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesReviewURL}")
    private String moviesReviewUrl;


    public MoviesReviewRestClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public Flux<Review> retrieveReviews(String movieId) {

        var url =
                UriComponentsBuilder.fromHttpUrl(moviesReviewUrl).queryParam("id", movieId).buildAndExpand().toUriString();
        return webClient.get().uri(url)
                .retrieve()
                .bodyToFlux(Review.class)
                .log();
    }
}
