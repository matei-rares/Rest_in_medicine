package com.mongou.advice;

import com.mongou.execeptions.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;


@ControllerAdvice
public class AdviceController {
    //406 e legat de headere, 422 e legat de body
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String sStackTrace = sw.toString();
        System.out.println("\n#############################################");
        //System.out.println(ex.getClass()+" "+ex.getMessage()+"\n"+ ex.getStackTrace());
        System.out.println(sStackTrace);
        System.out.println("#############################################\n");
        return new ResponseEntity<>(ex.getMessage(),ex.getHttpStatus());
    }






}
