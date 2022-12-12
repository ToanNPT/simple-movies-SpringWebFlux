package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService){
        this.movieInfoService = movieInfoService;
    }
    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> saveMovieInfo(@Validated @RequestBody MovieInfo movieInfo){
        return movieInfoService.addMovieInfo(movieInfo);
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMoviesInfo(@RequestParam("year") Optional<Integer> year){
        if(year.isPresent())
            return movieInfoService.getMoviesInfoByYear(year.get());
        return movieInfoService.findAll();
    }

    @GetMapping("movieinfos/{id}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable("id") String id){
        return movieInfoService.findById(id);
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable("id") String id,
                                                @Validated @RequestBody MovieInfo updated){
        return movieInfoService.updateMovieInfo(id, updated)
                .log()
                .map(movieInfo -> ResponseEntity.status(HttpStatus.OK).body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @DeleteMapping("/movieinfos/{id}")
    public Mono<Void> deleteMovieInfoById(@PathVariable("id") String id){
        return movieInfoService.deleteMovieInfo(id);
    }

}
