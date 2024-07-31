package com.singh.rupesh.webfluxDemo.webclient;

import com.singh.rupesh.webfluxDemo.dto.MultiplyRequestDto;
import com.singh.rupesh.webfluxDemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class HeaderTest extends BaseTest{

    @Autowired
    private WebClient webClient;

    @Test
    public void headersTest(){
        Mono<Response> responseMono =
                this.webClient
                        .post()
                        .uri("reactive-math/multiply")
                        .bodyValue(buildRequestDto(5, 2))
                        .headers(httpHeaders -> httpHeaders.set("some-Key", "some-Value"))
                        //setting basic authorization via headers but this is not ideal place to configure this
                        //.headers(httpHeaders -> httpHeaders.setBasicAuth("username", "password"))
                        .retrieve()
                        .bodyToMono(Response.class)
                        .doOnNext(System.out::println);

        StepVerifier.create(responseMono)
                .expectNextCount(1)
                .verifyComplete();


    }

    private MultiplyRequestDto buildRequestDto(int a, int b){
        MultiplyRequestDto dto = new MultiplyRequestDto();
        dto.setFirst(a);
        dto.setSecond(b);
        return dto;
    }
}