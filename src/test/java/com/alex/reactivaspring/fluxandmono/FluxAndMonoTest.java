package com.alex.reactivaspring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void flux_test() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
//                .concatWith(Flux.error(new RuntimeException("Some flux exception")))
                .concatWith(Flux.just("After error"));
        flux.map(s -> s.concat("flux"))
            .filter(s -> s.contains("Boot"))
            .subscribe(
                    System.out::println,
                    e -> System.err.println(e.getMessage()),
                    () -> System.out.println("Completed")
            );
    }

    @Test
    public void fluxTestElements() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(flux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .verifyComplete();
    }

    @Test
    public void fluxTestElementsWithError() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Some flux exception")))
                .log();

        StepVerifier.create(flux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Some flux exception")
                .verify();
    }

    @Test
    public void fluxTestCountOfElements() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(flux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_error() {
        StepVerifier.create(Mono.error(new RuntimeException("Some mono exception")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
