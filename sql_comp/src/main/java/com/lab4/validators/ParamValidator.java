package com.lab4.validators;

import com.lab4.Programari.Programare;
import com.lab4.exceptions.ParamsException;
import com.lab4.exceptions.ProgramareException;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ParamValidator {

    public static void validateDate(String date) {

    }
    public static int validateDateWithType(String date, String type) {
        int dateInt ;

        try{
            dateInt=Integer.parseInt(date);
        }
        catch (NumberFormatException e){
            throw new ParamsException("Date trebuie sa fie format din cifre", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(type.equals("year")){
            if( dateInt<0){
                throw new ParamsException("Date invalid pentru type="+type, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        else if(type.equals("month")){
            if(dateInt<0 || dateInt>12){
                throw new ParamsException("Date invalid pentru type="+type, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        else if(type.equals("day")){
            if( dateInt<0 || dateInt>31){
                throw new ParamsException("Date invalid pentru type="+type, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return dateInt;
    }
    public static void validateType(String type) {
        if (!(type.equals("year") || type.equals("month")|| type.equals("day"))){
            throw new ParamsException("Parametrul type e invalid", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
