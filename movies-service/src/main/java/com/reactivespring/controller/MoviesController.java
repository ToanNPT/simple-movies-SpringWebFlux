package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoWebClient;
import com.reactivespring.client.ReviewsWebClient;
import com.reactivespring.domain.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    @Autowired
    MoviesInfoWebClient moviesInfoWebClient;

    @Autowired
    ReviewsWebClient reviewsWebClient;

    @GetMapping("/{id}")
    public Mono<Movie> getById(@PathVariable("id") String id){
        return moviesInfoWebClient.retrieveMovieInfo(id)
                .flatMap(movieInfo -> {
                    var reviewsMonoList = reviewsWebClient.getReviewsByMovieId(id)
                            .collectList();

                    return reviewsMonoList.map(review -> new Movie(movieInfo, review));
                });
    }
}
