package com.alex.reactivaspring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> listFlux = Flux.fromIterable(names).log();

        StepVerifier.create(listFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxArray() {
        String[] names = new String[] {"adam", "anna", "jack", "jenny"};

        StepVerifier.create(Flux.fromArray(names))
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {

        Supplier<String> supplier = () -> "adam";
        Mono<String> mono = Mono.fromSupplier(supplier);
        System.out.println(supplier.get());
        StepVerifier.create(mono.log())
                .expectNext("adam")
                .verifyComplete();

    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier.create(integerFlux.log())
                .expectNext(1, 2,3,4,5)
                .verifyComplete();
    }

}
