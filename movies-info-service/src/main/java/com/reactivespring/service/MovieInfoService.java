package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exceptionHandler.NotFoundException;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MovieInfoService {
    MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository){
        this.movieInfoRepository = movieInfoRepository;
    }

    public Flux<MovieInfo> findAll(){
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo){
        log.info("addMovieInfo : {} " , movieInfo );
        return movieInfoRepository.save(movieInfo).log();
    }

    public Mono<MovieInfo> findById(String id){
        return movieInfoRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("there is no movie info with id" +id)));
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updated){
        Mono<MovieInfo> found = movieInfoRepository.findById(id);
        return found.flatMap(movieInfo -> {
                movieInfo.setCast(updated.getCast());
                movieInfo.setName(updated.getName());
                movieInfo.setYear(updated.getYear());
                movieInfo.setRelease_date(updated.getRelease_date());
                return movieInfoRepository.save(movieInfo);
            }).log();
    }

    public Mono<Void> deleteMovieInfo(String id){
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getMoviesInfoByYear(Integer year){
        return movieInfoRepository.findByYear(year).log();
    }
}
