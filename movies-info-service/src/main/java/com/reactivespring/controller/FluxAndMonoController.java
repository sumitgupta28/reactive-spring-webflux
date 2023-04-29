package com.reactivespring.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@Slf4j
public class FluxAndMonoController {


    @GetMapping("/flux")
    public Flux<Integer> flux(){
        return Flux.just(1,2,3).log();
    }


    @GetMapping("/mono")
    public Mono<String> mono(){
        return Mono.just("Hello World").log();
    }

    @GetMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> steam(){
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
