package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class HeaderException extends CustomException{

    public HeaderException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}