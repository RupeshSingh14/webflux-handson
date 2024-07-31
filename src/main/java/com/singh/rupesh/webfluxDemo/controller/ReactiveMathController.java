package com.singh.rupesh.webfluxDemo.controller;

import com.singh.rupesh.webfluxDemo.dto.MultiplyRequestDto;
import com.singh.rupesh.webfluxDemo.dto.Response;
import com.singh.rupesh.webfluxDemo.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("reactive-math")
public class ReactiveMathController {

    private final AtomicLong counter = new AtomicLong();
    private final Sinks.Many sink = Sinks.many().multicast().onBackpressureBuffer();

    @Autowired
    private ReactiveMathService mathService;

    @GetMapping("square/{input}")
    public Mono<Response> findSquare(@PathVariable int input){
        return this.mathService.findSquare(input);
    }

    @GetMapping("table/{input}")
    public Flux<Response> multiplicationTable(@PathVariable int input){
        return this.mathService.multiplicationTable(input);
    }

    @GetMapping(value = "table/{input}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // when media type is defined as TEXT_EVENT_STREAM_VALUE, all the values are processed and convert them
    // as json format as and when the item is emitted but when this media type is not defined, all the
    // values are collected as list and converted to json and then sent to client
    public Flux<Response> multiplicationTableStream(@PathVariable int input){
        return this.mathService.multiplicationTable(input);
    }


    @PostMapping("multiply")
    public Mono<Response> multiply(@RequestBody Mono<MultiplyRequestDto> requestDtoMono,
                                   @RequestHeader Map<String, String> headers){
        System.out.println(headers);
        return this.mathService.multiply(requestDtoMono);
    }

    @GetMapping("/send/{id}")
    public void test(@PathVariable String id) {
        Sinks.EmitResult result = sink.tryEmitNext("Hello World #" + counter.getAndIncrement() + id);

        if (result.isFailure()) {
            // do something here, since emission failed
        }
    }

    @RequestMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> sse() {
        return sink.asFlux().map(e -> ServerSentEvent.builder(e).build());
    }
}
