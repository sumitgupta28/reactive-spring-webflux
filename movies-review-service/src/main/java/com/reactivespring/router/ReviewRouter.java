package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {


    public static final String V_1_REVIEWS = "/v1/reviews";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {

        return route()
                .nest(path(V_1_REVIEWS), builder -> builder.POST("", reviewHandler::addReview)
                        .PUT("/{id}", reviewHandler::updateReview)
                        .DELETE("/{id}", reviewHandler::deleteReview)
                        .GET("/{id}", reviewHandler::getReviews)
                        .GET("", reviewHandler::getAllReview))
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello World")))
                .build();
    }
}
