package com.singh.rupesh.webfluxDemo.webtestclient;

import com.singh.rupesh.webfluxDemo.controller.ReactiveMathValidationController;
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

@WebFluxTest(ReactiveMathValidationController.class)
public class ErrorHandlingTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ReactiveMathService service;

    @Test
    public void errorHandlingTest(){

        Mockito.when(service.findSquare(Mockito.anyInt())).thenReturn(Mono.just(new Response(1)));

        this.client
                .get()
                .uri("/reactive-math/square/{input}/throw", 5)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("allowed range is 10 - 20")
                .jsonPath("$.errorCode").isEqualTo(100);

    }

}
