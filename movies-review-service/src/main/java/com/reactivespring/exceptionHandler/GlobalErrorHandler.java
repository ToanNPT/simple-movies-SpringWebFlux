package com.reactivespring.exceptionHandler;

import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        var errorMess = dataBufferFactory.wrap(ex.getMessage().getBytes(StandardCharsets.UTF_8));

        if(ex instanceof ReviewDataException){
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().writeWith(Mono.just(errorMess));
        }

        if(ex instanceof ReviewNotFoundException){
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().writeWith(Mono.just(errorMess));
        }
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().writeWith(Mono.just(errorMess));
    }
}
