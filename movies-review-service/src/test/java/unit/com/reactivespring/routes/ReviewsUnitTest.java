package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    final String BASE_URL = "/v1/reviews";
    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        Mockito.when(reviewReactiveRepository.save(ArgumentMatchers.isA(Review.class))).thenReturn(Mono.just(
                new Review(UUID.randomUUID().toString(), 1L, "Awesome Movie", 9.0)
        ));

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
    void addReview_Validation() {
        var review = new Review(null, 1L, "Awesome Movie", -9.0);


        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(review).exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("rating.negative : please pass a non-negative value");

        review = new Review(null, null, "Awesome Movie", 9.0);
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(review).exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("review.movieInfoId must not be null");

        review = new Review(null, null, null, -9.0);
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(review).exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("rating.negative : please pass a non-negative value,review.comment must not be null,review.movieInfoId must not be null");
    }

    @Test
    public void updateReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);


        Mockito.when(reviewReactiveRepository.save(ArgumentMatchers.isA(Review.class))).thenReturn(Mono.just(
                new Review(UUID.randomUUID().toString(), 1L, "Awesome Movie", 9.0)
        ));

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

        Mockito.when(reviewReactiveRepository.findById(ArgumentMatchers.isA(String.class))).thenReturn(Mono.just(
                new Review(UUID.randomUUID().toString(), 1L, "Good Movie", 7.5)
        ));

        Mockito.when(reviewReactiveRepository.save(ArgumentMatchers.isA(Review.class))).thenReturn(Mono.just(
                new Review(UUID.randomUUID().toString(), 1L, "Good Movie", 7.5)
        ));
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
    public void updateReview_ReviewNotFound() {


        String reviewId = UUID.randomUUID().toString();
        var review = new Review(reviewId, 1L, "Awesome Movie", 9.0);


        Mockito.when(reviewReactiveRepository.findById(ArgumentMatchers.isA(String.class))).thenReturn(Mono.empty());

        var uri = UriComponentsBuilder.fromUriString(BASE_URL).pathSegment(reviewId)
                .buildAndExpand().toUri();

        webTestClient.put()
                .uri(uri)
                .bodyValue(review).exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("Review not found for reviewId - " + reviewId);
    }
}
