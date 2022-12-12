package com.reactivespring.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handle(WebExchangeBindException exception){
        log.error("Error is caught by GlobalErrorHandler ", exception.getAllErrors(), exception);
        var errorMess = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

        log.error(errorMess);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMess);
    }

}
