package com.singh.rupesh.webfluxDemo.webtestclient;

import com.singh.rupesh.webfluxDemo.dto.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest // this is for Integration test as it creates all project beans before starting the test, we can @WebFLuxTest for unit tests alternatively
@AutoConfigureWebTestClient // Required for spring to auto inject test client bean
public class SimpleWebTestClientTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void stepVerifierTest(){
        Flux<Response> responseMono =
                this.client
                        .get()
                        .uri("/reactive-math/square/{input}", 5)
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .returnResult(Response.class)
                        .getResponseBody();

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getOutput() == 25)
                .verifyComplete();

    }

    @Test
    public void fluentAssertionTest(){
                this.client
                        .get()
                        .uri("/reactive-math/square/{input}", 5)
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(Response.class)
                        .value(r -> Assertions.assertThat(r.getOutput()).isEqualTo(25));

    }
}
