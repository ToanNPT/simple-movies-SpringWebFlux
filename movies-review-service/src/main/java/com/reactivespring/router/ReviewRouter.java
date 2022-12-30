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

    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler handler){

        return route()
                .nest(path("/v1/reviews"), builder -> {
                    builder.GET("", handler::filterReviews)
                            .POST("", handler::addReview)
                            .PUT("/{id}", handler::updateReview)
                            .DELETE("/{id}", handler::deleteReview)
                            .GET("/stream/{id}", handler::getReviewsByMovieId);
                })
                .build();
    }
}
