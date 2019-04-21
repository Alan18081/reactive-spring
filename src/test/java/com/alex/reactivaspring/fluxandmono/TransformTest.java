package com.alex.reactivaspring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class TransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> flux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(flux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> flux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(flux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> Flux.fromIterable(convertToList(s)))
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMapAsync() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapAsyncOrder() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
}
