package com.reactivespring.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {
    public Mono<ServerResponse> addReview(){

        return ServerResponse.ok().bodyValue("From post route ");
    }
}
