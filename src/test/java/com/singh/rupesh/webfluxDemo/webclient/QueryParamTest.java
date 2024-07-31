package com.singh.rupesh.webfluxDemo.webclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Map;

public class QueryParamTest extends BaseTest {

    @Autowired
    private WebClient webClient;

    final String queryString = "http://localhost:8080/jobs/search?count={count}&page={page}";

    //passing query param by a pre defined uri
    @Test
    public void queryParamsTest1() {
        URI uri = UriComponentsBuilder.fromUriString(queryString)
                .build(10, 20);

        Flux<Integer> flux = this.webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Integer.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();


    }

    //building the uri and passing the value of query param
    @Test
    public void queryParamsTest2() {
        Flux<Integer> flux = this.webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("jobs/search").query("count={count}&page={page}").build(10,20))
                .retrieve()
                .bodyToFlux(Integer.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }

    //building the URI and passing map of values for query params
    @Test
    public void queryParamsTest3() {

        Map<String, Integer> map = Map.of(
                "count", 10,
                "page", 20
        );

        Flux<Integer> flux = this.webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("jobs/search").query("count={count}&page={page}").build(map))
                .retrieve()
                .bodyToFlux(Integer.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }

}
