package com.singh.rupesh.webfluxDemo.webtestclient;

import com.singh.rupesh.webfluxDemo.controller.ReactiveMathController;
import com.singh.rupesh.webfluxDemo.dto.MultiplyRequestDto;
import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.service.ReactiveMathService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ReactiveMathController.class)
public class ControllerPostTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ReactiveMathService service;

    @Test
    public void postTest(){

        Mockito.when(service.multiply(Mockito.any())).thenReturn(Mono.just(new Response(1)));

        this.client
                .post()
                .uri("/reactive-math/multiply")
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBasicAuth("user", "password"))
                .headers(h -> h.set("some-value", "some-password"))
                .bodyValue(new MultiplyRequestDto())
                .exchange()
                .expectStatus().is2xxSuccessful();

    }



}
