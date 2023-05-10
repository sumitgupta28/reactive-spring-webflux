package com.reactivespring.handler;


import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {


    private final ReviewReactiveRepository reviewReactiveRepository;
    @Autowired
    private Validator validator;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {

        var constraintViolation = validator.validate(review);
        log.info(" Constraint violation : {} ", constraintViolation);
        if (constraintViolation.size() > 0) {
            var errorMessage = constraintViolation.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            log.info(" Constraint violation Messages : {} ", errorMessage);
            throw new ReviewDataException(errorMessage);
        }

    }


    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {

        var reviewId = serverRequest.pathVariable("id");
        log.info(" update reviewId - {} ", reviewId);
        // var existingReview = reviewReactiveRepository.findById(reviewId);

        return reviewReactiveRepository.findById(reviewId).flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .map(requestReview -> {
                            review.setComment(requestReview.getComment());
                            review.setRating(requestReview.getRating());
                            return review;
                        }).flatMap(reviewReactiveRepository::save)
                        .flatMap(ServerResponse.ok()::bodyValue))
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for reviewId - " + reviewId)));

    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        log.info(" delete reviewId - {} ", reviewId);
        return reviewReactiveRepository.findById(reviewId).flatMap(review ->
                reviewReactiveRepository.deleteById(reviewId)
        ).then(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> getAllReview(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            var reviews = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviews);
        } else {
            var reviews = reviewReactiveRepository.findAll();
            return buildReviewsResponse(reviews);
        }
    }

    private Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviews) {
        return ServerResponse.ok()
                .body(reviews, Review.class);
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        log.info(" get reviewId - {} ", reviewId);
        var existingReview = reviewReactiveRepository.findById(reviewId);
        if (null != existingReview) {
            return ServerResponse.ok()
                    .body(existingReview, Review.class);
        }
        return ServerResponse.notFound().build();
    }
}
