package com.lab4.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorSqlException  extends CustomException{

    public DoctorSqlException(String message, HttpStatus statusCode)
    {
        super(message,statusCode);
    }

}