package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class PacientSqlException extends CustomException{

    public PacientSqlException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}