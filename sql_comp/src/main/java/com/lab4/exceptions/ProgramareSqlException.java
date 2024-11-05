package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class ProgramareSqlException extends CustomException{

    public ProgramareSqlException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}