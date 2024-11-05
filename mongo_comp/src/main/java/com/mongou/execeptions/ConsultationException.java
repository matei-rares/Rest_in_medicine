package com.mongou.execeptions;

import org.springframework.http.HttpStatus;

public class ConsultationException extends CustomException {
    public ConsultationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
