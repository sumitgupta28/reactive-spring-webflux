package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovie(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Mono<MovieInfo> getMovieById(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }

    public Mono<Void> deleteMovieById(String movieInfoId) {
        return movieInfoRepository.deleteById(movieInfoId);
    }

    public Flux<MovieInfo> getAllMovies() {
        return movieInfoRepository.findAll();
    }


    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {

        return movieInfoRepository.findById(id).flatMap(
                movieInfo -> {
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                    return movieInfoRepository.save(movieInfo);
                });
    }

    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}
