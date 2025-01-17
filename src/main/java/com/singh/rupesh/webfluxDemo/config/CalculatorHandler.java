package com.singh.rupesh.webfluxDemo.config;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Service
public class CalculatorHandler {

    // created multiple handlers intentionally
    // calculator/{a}/{b}
    public Mono<ServerResponse> additionHandler(ServerRequest request) {
        /*
        int a = getValue(request, "a");
        int b = getValue(request, "b");
        return ServerResponse.ok().bodyValue(a - b);
        */

        return process(request, (a,b) -> ServerResponse.ok().bodyValue(a + b));
    }

    public Mono<ServerResponse> subtractionHandler(ServerRequest request) {
        return process(request, (a,b) -> ServerResponse.ok().bodyValue(a - b));
    }

    public Mono<ServerResponse> multiplicationHandler(ServerRequest request) {
        return process(request, (a,b) -> ServerResponse.ok().bodyValue(a * b));
    }

    public Mono<ServerResponse> divisionHandler(ServerRequest request) {
        return process(request, (a,b) -> {
            return b != 0 ? ServerResponse.ok().bodyValue(a / b) : ServerResponse.badRequest().bodyValue("b cannot be 0");
                });
    }

    private Mono<ServerResponse> process(ServerRequest request,
                                         BiFunction<Integer, Integer, Mono<ServerResponse>> opLogic) {
        int a = getValue(request, "a");
        int b = getValue(request, "b");
        return opLogic.apply(a,b);
    }


    private int getValue(ServerRequest request, String key) {
        return Integer.parseInt(request.pathVariable(key));
    }
}
