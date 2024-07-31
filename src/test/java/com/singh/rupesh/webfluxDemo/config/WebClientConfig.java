package com.singh.rupesh.webfluxDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                // best way to pass any default headers or token as they are cross cutting concerns
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("username", "password"))
                .filter(this::sessionToken) //.filter((clientRequest, exchangeFunction) -> sessionToken(clientRequest, exchangeFunction))
                .build();
    }

    // building the ExchangeFilterFunction() for passing it to the filter
    /*private Mono<ClientResponse> sessionToken(ClientRequest request, ExchangeFunction ex){
        System.out.println("generating session token");
        ClientRequest clientRequest = ClientRequest.from(request).headers(h -> h.setBearerAuth("some-lengthy-jwt")).build();
        return ex.exchange(clientRequest);
    }*/

    //for influencing kind of authorization at run time on the fly like simulating a cross cutting concern where making payment calls needs short lived
    //newly generation jwt token but all other non trivial calls need basic auth via passing of attribute via service class.
    private Mono<ClientResponse> sessionToken(ClientRequest request, ExchangeFunction ex){
        // attribute: auth -> basic or oauth
        ClientRequest clientRequest = request.attribute("auth")
                .map(v -> v.equals("basic") ? withBasicAuth(request) : withOAuth(request))
                .orElse(request);
        return ex.exchange(clientRequest);
    }

    //since client request is immutable, so always create a new request out of it
    private ClientRequest withBasicAuth(ClientRequest request){
        return ClientRequest.from(request)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("username", "password"))
                .build();
    }

    private ClientRequest withOAuth(ClientRequest request){
        return ClientRequest.from(request)
                .headers(httpHeaders -> httpHeaders.setBearerAuth("some-lengthy-token"))
                .build();
    }




}
