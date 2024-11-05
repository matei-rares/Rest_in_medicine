package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class ProgramareException extends CustomException{

    public ProgramareException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}