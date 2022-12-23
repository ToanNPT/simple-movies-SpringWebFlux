package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.nio.file.Path;

import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler handler){

        return route()
                .nest(path("/v1/reviews"), builder -> {
                    builder.GET("", request -> handler.filterReviews(request))
                            .POST("", request -> handler.addReview(request))
                            .PUT("/{id}", request -> handler.updateReview(request))
                            .DELETE("/{id}", request -> handler.deleteReview(request));
                })
                .build();
    }
}
