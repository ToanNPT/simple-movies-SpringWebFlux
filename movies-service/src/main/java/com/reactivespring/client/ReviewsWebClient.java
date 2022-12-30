package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewsWebClient {
    private WebClient webClient;

    @Value("${restClient.reviewsURl}")
    private String valueUrl;

    public ReviewsWebClient(WebClient webClient){
        this.webClient = webClient;
    }

    public Flux<Review> getReviewsByMovieId(String movieId){
        var url = UriComponentsBuilder.fromHttpUrl(valueUrl)
                .queryParam("movieInfo", movieId)
                .buildAndExpand();
        return webClient.get()
                .uri(url.toUriString())
                .retrieve()
                .bodyToFlux(Review.class);
    }
}
