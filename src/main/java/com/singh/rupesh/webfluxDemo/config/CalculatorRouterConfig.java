package com.singh.rupesh.webfluxDemo.config;

import com.singh.rupesh.webfluxDemo.exceptions.InputValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

/*
Building a calculator
 */
@Configuration
public class CalculatorRouterConfig {

    @Autowired
    private CalculatorHandler handler;

    // this function will intercept any call to endpoint starting with "calculator" on this application port and route to downstream
    @Bean
    public RouterFunction<ServerResponse> highLevelCalculatorRouter() {
        return RouterFunctions.route()
                .path("calculator", this::serverResponseRouterFunction)
                .build();
    }


    // Request predicates can apply filters on endpoint (path regex, headers, media type and more) to route on the conditions basis
    private RouterFunction<ServerResponse> serverResponseRouterFunction() {
        return RouterFunctions.route()
                .GET("{a}/{b}", isOperation("+") , handler::additionHandler)
                .GET("{a}/{b}", isOperation("-") , handler::subtractionHandler)
                .GET("{a}/{b}", isOperation("*") , handler::multiplicationHandler)
                .GET("{a}/{b}", isOperation("/"), handler::divisionHandler)
                .GET("{a}/{b}", request -> ServerResponse.badRequest().bodyValue("OP key provided is not allowed"))
                .build();
    }

    // extracts the header and matches the key's value for the defined symbol and return a boolean
    private RequestPredicate isOperation(String operation) {
        return RequestPredicates.headers(headers -> operation.equals(headers.asHttpHeaders().toSingleValueMap().get("OP")));
    }


}
