package com.singh.rupesh.webfluxDemo.config;

import com.singh.rupesh.webfluxDemo.dto.FailedValidationResponse;
import com.singh.rupesh.webfluxDemo.exceptions.InputValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

/*
Function endpoint/API's. They are light weight alternative to controller based class declaration. Both
types of declaration can co exist. The URI's defined here are routed to a class responsible to
extract all info from Request and make further processing and return response.
We can have multiple router function beans which handles different set of endpoints or makes a nested functions
 */
@Configuration
public class RouterConfig {

    @Autowired
    private RequestHandler requestHandler;

    /*
    with addition of this bean which is a function calling another function (nested function), if path matches, then it will route to the defined method.
    since this will acts as a bean now, all the downstream functions need not be declared as bean but as a private method.
     */
    @Bean
    public RouterFunction<ServerResponse> highLevelRouter() {
        return RouterFunctions.route()
                .path("router", this::serverResponseRouterFunction)
                .build();
    }


    // Request predicates can apply filters on endpoint (path regex, headers, media type and more) to route on the conditions basis
    private RouterFunction<ServerResponse> serverResponseRouterFunction() {
        return RouterFunctions.route()
                .GET("square/{input}", RequestPredicates.path("*/1?").or(RequestPredicates.path("*/20")), requestHandler::squareHandler)
                .GET("square/{input}", request -> ServerResponse.badRequest().bodyValue("only 10 -20 allowed"))
                .GET("table/{input}", requestHandler::tableHandler)
                .GET("stream/table/{input}", requestHandler::tableHandlerStreaming)
                .POST("multiply", requestHandler::multiplyHandler)
                .GET("square/{input}/validation", requestHandler::squareHandlerWithValidation)
                .onError(InputValidationException.class, exceptionHandler())
                .build();
    }

    /*
    this method gets deprecated after addition of nested function (highLevelRouter) and is later replaced with above method
    which is a private method and has does not contains word "router" in its URI.
     */
    @Bean
    public RouterFunction<ServerResponse> serverResponseRouterFunction1() {
        return RouterFunctions.route()
                .GET("router1/square/{input}", requestHandler::squareHandler)
                .GET("router1/table/{input}", requestHandler::tableHandler)
                .GET("router1/stream/table/{input}", requestHandler::tableHandlerStreaming)
                .POST("router1/multiply", requestHandler::multiplyHandler)
                .GET("router1/square/{input}/validation", requestHandler::squareHandlerWithValidation)
                .onError(InputValidationException.class, exceptionHandler())
                .build();
    }

    // Bi function for error handling on input validation.
    private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> exceptionHandler() {
        return (err, req) -> {
            InputValidationException ex = (InputValidationException) err;
            FailedValidationResponse response = new FailedValidationResponse();
            response.setInput(ex.getInput());
            response.setMessage(ex.getMessage());
            response.setErrorCode(ex.getErrorCode());
            return ServerResponse.badRequest().bodyValue(response);
        };
    }

}