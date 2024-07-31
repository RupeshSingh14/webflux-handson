package com.singh.rupesh.webfluxDemo.controller;

import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.exceptions.InputValidationException;
import com.singh.rupesh.webfluxDemo.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("reactive-math")
public class ReactiveMathValidationController {

    @Autowired
    private ReactiveMathService mathService;

    @GetMapping("square/{input}/throw")
    public Mono<Response> findSquare(@PathVariable int input){
        if(input < 10 || input > 20)
            throw new InputValidationException(input);
        return this.mathService.findSquare(input);
    }

    @GetMapping("square/{input}/mono-error")
    public Mono<Response> monoError(@PathVariable int input){
        return Mono.just(input)
                .handle((integer, sink) -> {
                    if(integer >= 10 && integer <= 20) {
                        sink.next(integer);
                    }
                    else
                        sink.error(new InputValidationException(integer));
                })
                .cast(Integer.class)
                .flatMap(i -> this.mathService.findSquare(i));
    }

    @GetMapping("square/{input}/mono-badRequest")
    public Mono<ResponseEntity<Response>> badRequest(@PathVariable int input){
        return Mono.just(input)
                .filter(i -> i >= 10 && i <= 20)
                .flatMap(i -> this.mathService.findSquare(i))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}