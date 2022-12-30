package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Component
public class ReviewHandler {
    private ReviewReactiveRepository repository;

    private Sinks.Many<Review> reviewsSinks = Sinks.many().replay().latest();

    @Autowired
    Validator validator;

    public ReviewHandler(ReviewReactiveRepository repository){
        this.repository = repository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request){
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(review -> repository.save(review))
                .doOnNext(review -> reviewsSinks.tryEmitNext(review))
                .flatMap(review -> ServerResponse.ok().bodyValue(review));
    }

    public Mono<ServerResponse> getAllReview(){
        return ServerResponse.ok().body(repository.findAll(), Review.class);
    }

    public Mono<ServerResponse> filterReviews(ServerRequest request){
        var movieInfoId = request.queryParam("movieInfo");
        if(!movieInfoId.isPresent()){
            return getAllReview();
        }
        else{
            return ServerResponse.ok().body(repository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get())), Review.class);
        }
    }

    public Mono<ServerResponse> updateReview(ServerRequest request){
        var id = request.pathVariable("id");
        var existedReview = repository.findById(id)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("can not found review id " + id)));

        return existedReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(repository::save)
                        .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request){
        var id = request.pathVariable("id");
        var existedReview = repository.findById(id)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("can not found review id " + id)));

        return existedReview
                .flatMap(review -> repository.delete(review))
                .then(ServerResponse.noContent().build());
    }

    public void validate(Review review){
        var constraintViolations = validator.validate(review);
        if(constraintViolations.size() > 0) {
            var messString = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(messString);
        }
    }

    public Mono<ServerResponse> getReviewsByMovieId(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewsSinks
                                .asFlux()
                                .filter(review -> review.getMovieInfoId() == id)
                        , Review.class
                );
    }
}
