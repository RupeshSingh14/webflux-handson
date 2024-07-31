package com.singh.rupesh.webfluxDemo.webclient;

import com.singh.rupesh.webfluxDemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class GetFluxResponseTest extends BaseTest {

    @Autowired
    private WebClient webClient;

    @Test
    public void stepVerifierFluxTest(){
        Flux<Response> responseFlux =
                this.webClient
                        .get()
                        .uri("reactive-math/table/{input}", 5)
                        .retrieve()
                        .bodyToFlux(Response.class)
                        .doOnNext(System.out::println); //Flux<Response>

        StepVerifier.create(responseFlux)
                .expectNextCount(10)
                .verifyComplete();

    }


    @Test
    public void stepVerifierStreamTest(){
        Flux<Response> responseFlux =
                this.webClient
                        .get()
                        .uri("reactive-math/table/{input}/stream", 5)
                        .retrieve()
                        .bodyToFlux(Response.class)
                        .doOnNext(System.out::println); //Flux<Response>

        StepVerifier.create(responseFlux)
                .expectNextCount(10)
                .verifyComplete();

    }

}
