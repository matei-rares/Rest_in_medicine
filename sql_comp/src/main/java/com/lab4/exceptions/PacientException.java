package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class PacientException extends CustomException{

    public PacientException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}