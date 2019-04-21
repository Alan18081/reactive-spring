package com.alex.reactivaspring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFilterTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(name -> name.length() > 4)
                .log();

        namesFlux.subscribe(System.out::println);

        StepVerifier.create(namesFlux)
                .expectNext("jenny")
                .verifyComplete();
    }
}
