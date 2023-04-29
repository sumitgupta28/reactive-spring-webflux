package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService){
        this.movieInfoService=movieInfoService;
    }

    @PostMapping(value = "/movieinfos", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        log.info(" Incoming value {} " , movieInfo);
        return movieInfoService.addMovie(movieInfo).log();

    }


    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById_approach2(@PathVariable("id") String id) {
        log.info(" Incoming value id {} " , id);

        return movieInfoService.getMovieById(id)
                .map(movieInfo -> ResponseEntity.ok()
                        .body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }


    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        log.info(" Incoming value id {} " , id);
        return movieInfoService.deleteMovieById(id);
    }


    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovies() {
        return movieInfoService.getAllMovies();
    }



    @PutMapping(value = "/movieinfos/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo,@PathVariable("id") String id){
        log.info(" Incoming value Movie Id {} and  MovieInfo {} " ,id, movieInfo);
        return movieInfoService.updateMovieInfo(movieInfo,id).log();

    }

}
