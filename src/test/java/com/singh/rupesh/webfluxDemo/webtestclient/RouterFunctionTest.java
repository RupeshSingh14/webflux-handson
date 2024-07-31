package com.singh.rupesh.webfluxDemo.webtestclient;

import com.singh.rupesh.webfluxDemo.config.RequestHandler;
import com.singh.rupesh.webfluxDemo.config.RouterConfig;
import com.singh.rupesh.webfluxDemo.dto.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@WebFluxTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = RouterConfig.class) // loading the RouterConfig class explicitly to use it here
public class RouterFunctionTest {

    @Autowired
    private WebTestClient client;

    //This can only be autowired since we have added ContextConfiguration at class level
    @Autowired
    private ApplicationContext ctx; //as we have multiple routers this will open access to all of them

    @MockBean
    private RequestHandler requestHandler;

    @BeforeAll
    public void setClient(){
        this.client = WebTestClient.bindToApplicationContext(ctx).build();   //setting the webclient
        //WebTestClient can be used to do integration tests as well as call any external api for testing like below
        /*WebTestClient.bindToServer().baseUrl("http://localhost:8090").build()
                .get()
                .uri()..
         */
    }

    @Test
    public void test(){
        Mockito.when(requestHandler.squareHandler(Mockito.any())).thenReturn(ServerResponse.ok().bodyValue(new Response(225)));

        this.client
                .get()
                .uri("/router/square/{input}", 15)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Response.class)
                .value(r -> Assertions.assertThat(r.getOutput()).isEqualTo(225));
    }
}
