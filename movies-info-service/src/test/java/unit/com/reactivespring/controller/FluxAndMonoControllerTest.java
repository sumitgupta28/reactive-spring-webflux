package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    void flux2() {
     var flux=   webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(1,2,3).verifyComplete();

    }


    @Test
    void stream() {
        var flux=   webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(0L,1L,2L,3L).thenCancel().verify();

    }


    @Test
    void flux3() {
      webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Integer.class)
                .consumeWith(integerFluxExchangeResult -> {
                   var responseBody= integerFluxExchangeResult.getResponseBody();
                   assertNotNull (responseBody);
                });



    }

    @Test
    void mono() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class).isEqualTo("Hello World");
    }

    @Test
    void mono1() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class).consumeWith(
                        stringEntityExchangeResult -> {
                         var body=   stringEntityExchangeResult.getResponseBody();
                         assertNotNull(body);
                         assertEquals("Hello World",body);
                        }
                );
    }

    @Test
    void steam() {
    }
}