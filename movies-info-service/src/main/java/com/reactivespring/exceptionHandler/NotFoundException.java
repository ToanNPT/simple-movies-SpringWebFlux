package com.reactivespring.exceptionHandler;

import ch.qos.logback.core.encoder.EchoEncoder;

public class NotFoundException extends RuntimeException {
    private String message;

    public NotFoundException(String message){
        super(message);
        this.message = message;
    }
}
