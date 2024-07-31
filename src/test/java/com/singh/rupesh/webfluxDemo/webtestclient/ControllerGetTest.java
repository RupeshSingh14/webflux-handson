package com.singh.rupesh.webfluxDemo.webtestclient;

import com.singh.rupesh.webfluxDemo.controller.ParamsController;
import com.singh.rupesh.webfluxDemo.controller.ReactiveMathController;
import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.service.ReactiveMathService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

//@WebFluxTest(ReactiveMathController.class) // class we want to unit test
@WebFluxTest(controllers = {ReactiveMathController.class, ParamsController.class})
//passing 2 controller classes for unit testing
public class ControllerGetTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ReactiveMathService mathService; // mocking the bean since it will not be automatically injected

    @Test
    public void singleResponseTest(){

        //mocking service class
        Mockito.when(mathService.findSquare(Mockito.anyInt())).thenReturn(Mono.just(new Response(25)));

        //unit test for controller
        this.client
                .get()
                .uri("/reactive-math/square/{input}", 5)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Response.class)
                .value(r -> Assertions.assertThat(r.getOutput()).isEqualTo(25));
    }

    @Test
    public void listResponseTest(){

        Flux<Response> flux = Flux.range(1, 3) // for simulating expected output
                .map(Response::new);

        //mocking service class
        Mockito.when(mathService.multiplicationTable(Mockito.anyInt())).thenReturn(flux);

        //unit test for controller
        this.client
                .get()
                .uri("/reactive-math/table/{input}", 5)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Response.class)
                .hasSize(3);
    }

    //for testing streaming api
    @Test
    public void StreamingResponseTest(){

        Flux<Response> flux = Flux.range(1, 3) // for simulating expected output
                .map(Response::new)
                .delayElements(Duration.ofMillis(100)); //intentional delay

        //mocking service class
        Mockito.when(mathService.multiplicationTable(Mockito.anyInt())).thenReturn(flux);

        //unit test for controller
        this.client
                .get()
                .uri("/reactive-math/table/{input}/stream", 5)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE)
                .expectBodyList(Response.class)
                .hasSize(3);
    }

    @Test
    public void paramTest(){

        Map<String, Integer> map = Map.of(
                "count", 10,
                "page", 20
        );

        this.client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/jobs/search").query("count={count}&page={page}").build(map))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(2)
                .contains(10,20);

    }


}
