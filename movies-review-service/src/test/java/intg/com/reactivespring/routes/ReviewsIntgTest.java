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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void updateReview() {
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
                    review.setReviewId(movieReview.getReviewId());
                    review.setMovieInfoId(movieReview.getMovieInfoId());
                });

        review.setRating(7.5);
        review.setComment("Good Movie");

        var uri = UriComponentsBuilder.fromUriString(BASE_URL + "/").path(review.getReviewId())
                .buildAndExpand().toUri();

        webTestClient.put()
                .uri(uri)
                .bodyValue(review).exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(movieReviewEntityExchangeResult -> {
                    var movieReview = movieReviewEntityExchangeResult.getResponseBody();
                    assert movieReview != null;
                    assert movieReview.getMovieInfoId() != null;
                    assert movieReview.getReviewId() != null;
                    assertEquals("Good Movie", movieReview.getComment());
                    assertEquals(7.5, movieReview.getRating());
                });
    }


    @Test
    public void getReviews() {
        webTestClient.get().uri(BASE_URL)
                .exchange().expectStatus().isOk().expectBodyList(Review.class)
                .hasSize(3);
    }


    @Test
    public void getReviewsbyMovieInfoId() {


        var uri = UriComponentsBuilder.fromUriString(BASE_URL).queryParam("movieInfoId", 1L)
                .buildAndExpand().toUri();

        webTestClient.get().uri(uri)
                .exchange().expectStatus().isOk().expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    public void getReviewById() {
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
                    review.setReviewId(movieReview.getReviewId());

                });

        webTestClient.get().uri(BASE_URL + "/{id}", review.getReviewId())
                .exchange().expectStatus().isOk().expectBody(Review.class).consumeWith(
                        reviewEntityExchangeResult -> {
                            var movieReview = reviewEntityExchangeResult.getResponseBody();
                            assert movieReview != null;
                            assert movieReview.getMovieInfoId() != null;
                            assert movieReview.getReviewId() != null;
                        }
                );


    }


    @Test
    public void deleteById() {
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
                    review.setReviewId(movieReview.getReviewId());

                });

        webTestClient.delete().uri(BASE_URL + "/{id}", review.getReviewId())
                .exchange().expectStatus().isNotFound();
    }

    @BeforeEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }
}
