package com.reactivespring.unit;

import com.reactivespring.controller.FluxAndMonoController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;

@WebFluxTest(controllers = FluxAndMonoController.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient clientTest;

    @Test
    void getFlux() {
        var response = clientTest.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    var res = listEntityExchangeResult.getResponseBody();
                    assert (Objects.requireNonNull(res).size() == 3);
                });
    }

    @Test
    void getMono() {
        var response = clientTest.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var res = stringEntityExchangeResult.getResponseBody();
                    assert (res.equals("hello mono"));
                });
    }

    @Test
    void streamAPI() {
        var responseBody = clientTest.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(0L, 1L, 2L)
                .thenCancel()
                .verify();
    }
}