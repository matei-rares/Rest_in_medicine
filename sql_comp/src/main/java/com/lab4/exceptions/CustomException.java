package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
    private final HttpStatus status;

    public CustomException(String message,HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}