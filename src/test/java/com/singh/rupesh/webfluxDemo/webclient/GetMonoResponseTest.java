package com.singh.rupesh.webfluxDemo.webclient;

import com.singh.rupesh.webfluxDemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class GetMonoResponseTest extends BaseTest{

    @Autowired
    private WebClient webClient;

    @Test
    public void blockTest(){
        Response response =
                this.webClient
                .get()
                .uri("reactive-math/square/{input}", 5)
                .retrieve()
                .bodyToMono(Response.class)
                .block(); //used just for testing

        System.out.println(response);

    }

    // step verifier is best way to test reactive code. It excludes need of usage of block
    @Test
    public void stepVerifierTest(){
        Mono<Response> responseMono =
                this.webClient
                        .get()
                        .uri("reactive-math/square/{input}", 5)
                        .retrieve()
                        .bodyToMono(Response.class); //Mono<Response>

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getOutput() == 25)
                .verifyComplete();

    }
}