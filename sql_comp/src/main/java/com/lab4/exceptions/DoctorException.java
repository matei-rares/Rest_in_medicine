package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorException extends CustomException{

    public DoctorException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}
