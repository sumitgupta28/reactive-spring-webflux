package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    String BASE_URL = "/v1/reviews";

    @BeforeEach
    void setup() {

        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }


    @Test
    public void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(review).exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(movieReviewEntityExchangeResult -> {
                    var movieReview = movieReviewEntityExchangeResult.getResponseBody();
                    assert movieReview != null;
                    assert movieReview.getMovieInfoId() != null;
                    assert movieReview.getReviewId() != null;

                });
    }

    @Test
    public void getReviews() {
        webTestClient.get().uri(BASE_URL)
                .exchange().expectStatus().isOk().expectBodyList(Review.class)
                .hasSize(3);
    }


    @BeforeEach
    void tearDown() {
        reviewReactiveRepository.deleteAll();
    }
}
