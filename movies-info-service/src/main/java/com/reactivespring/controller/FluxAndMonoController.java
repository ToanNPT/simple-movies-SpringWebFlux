package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.Duration;

@RestController
public class FluxAndMonoController {
    @GetMapping("flux")
    public Flux<Integer> getFlux(){
        return Flux.just(1,2,3).log();
    }

    @GetMapping("mono")
    public Mono<String> getMono(){
        return Mono.just("hello mono").log();
    }

    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> streamAPI(){
        int a = 1111;
        int b = 200;
        return Flux.interval(Duration.ofSeconds(1)).log();
    }

}
