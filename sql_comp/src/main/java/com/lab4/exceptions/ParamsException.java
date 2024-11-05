package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class ParamsException extends CustomException{

    public ParamsException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }
}
