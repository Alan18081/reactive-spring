package com.alex.reactivaspring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxStreamTest {

    @Autowired
    WebTestClient client;

    @Test
    public void fluxStream() {
        Flux<Long> stream = client.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(stream)
                .expectSubscription()
                .expectNext(1l)
                .expectNext(2l)
                .expectNext(3l)
                .thenCancel()
                .verify();
    }

    @Test
    public void monoTest() {
        client.get().uri("/mono").accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(integerEntityExchangeResult -> {
                    assertEquals(new Integer(1), integerEntityExchangeResult.getResponseBody());
                });
    }

}
