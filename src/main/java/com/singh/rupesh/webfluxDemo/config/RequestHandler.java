package com.singh.rupesh.webfluxDemo.config;

import com.singh.rupesh.webfluxDemo.dto.MultiplyRequestDto;
import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.exceptions.InputValidationException;
import com.singh.rupesh.webfluxDemo.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;

@Service
public class RequestHandler {

    @Autowired
    private ReactiveMathService mathService;

    //for handling functional endpoint for square of a number
    public Mono<ServerResponse> squareHandler(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        Mono<Response> responseMono = this.mathService.findSquare(input);
        return ServerResponse.ok().body(responseMono, Response.class);
    }

    //for handling functional endpoint for table of a number
    // return type will remain Mono of ServerResponse which internally has flux of data
    // this can be logically represented as Mono<ServerResponse<Flux<Response>>>
    public Mono<ServerResponse> tableHandler(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        Flux<Response> responseFlux = this.mathService.multiplicationTable(input);
        return ServerResponse.ok().body(responseFlux, Response.class);
    }

    //for handling functional endpoint for table of a number in streaming
    public Mono<ServerResponse> tableHandlerStreaming(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        Flux<Response> responseFlux = this.mathService.multiplicationTable(input);
        return ServerResponse.
                ok()
                .contentType(MediaType.TEXT_EVENT_STREAM) // makes the response streaming
                .body(responseFlux, Response.class);
    }

    //for handling functional endpoint for POST request with header
    public Mono<ServerResponse> multiplyHandler(ServerRequest serverRequest) {
        Mono<MultiplyRequestDto> requestDtoMono = serverRequest.bodyToMono(MultiplyRequestDto.class);
        if (serverRequest.headers().firstHeader("name").equals("Rupesh")) {
            Mono<Response> responseMono = this.mathService.multiply(requestDtoMono);
            return ServerResponse.
                    ok()
                    .body(responseMono, Response.class);
        } else {
            return ServerResponse
                    .badRequest()
                    .build();
        }

    }

    //for handling functional endpoint for square of a number with validation and error handling
    public Mono<ServerResponse> squareHandlerWithValidation(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        if(input < 10 || input > 20) {
            return Mono.error(new InputValidationException(input));
        }
        Mono<Response> responseMono = this.mathService.findSquare(input);
        return ServerResponse.ok().body(responseMono, Response.class);
    }













}