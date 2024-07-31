package com.singh.rupesh.webfluxDemo.webclient;

import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.exceptions.InputValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ExchangeTest extends BaseTest{

    @Autowired
    private WebClient webClient;

    // exchange is like retrieve but it gives more control over the request like additional info http status code or any manipulation
    @Test
    public void badRequestTest(){
        Mono<Object> responseMono =
                this.webClient
                        .get()
                        .uri("reactive-math/square/{input}/throw", 5)
                        .exchangeToMono(this::exchange)
                        //.retrieve()
                        //.bodyToMono(Response.class)
                        .doOnNext(System.out::println)
                        .doOnError(err -> System.out.println(err.getMessage())); //Flux<Response>

        StepVerifier.create(responseMono)
                .expectNextCount(1)
                .expectComplete();

    }

    private Mono<Object> exchange(ClientResponse cr) {
        if(cr.rawStatusCode() == 400)
            return cr.bodyToMono(InputValidationException.class);
        else
            return cr.bodyToMono(Response.class);
    }

}
