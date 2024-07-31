package com.singh.rupesh.webfluxDemo.webclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CalculatorTest extends BaseTest{

    @Autowired
    private WebClient webClient;

    private static final String FORMAT = "%d %s %d = %s";
    private static final int A = 10;

    @Test
    public void calculatorTest() {

        Flux<String> flux = Flux.range(1, 5)
                .flatMap(b -> Flux.just("+", "-", "*", "/")
                        .flatMap(op -> send(b, op)))
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(20)
                .verifyComplete();
    }

    private Mono<String> send(int b, String op){
        return this.webClient
                .get()
                .uri("calculator/{a}/{b}", A, b)
                .headers(httpHeaders -> httpHeaders.set("OP", op))
                .retrieve()
                .bodyToMono(String.class)
                .map(v -> String.format(FORMAT, A, op, b, v));
    }

}
