package com.alex.reactivaspring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();

    }

    @Test
    public void backPressure() {
        Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(
          element -> System.out.println("Element is: " + element),
          err -> System.err.println("Error: " + err),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2)
        );
    }

}
