package com.lab4.advice;

import com.lab4.exceptions.CustomException;
import com.lab4.exceptions.DoctorException;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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













    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationExeptions(ConstraintViolationException ex){

        return new ResponseEntity<>(ex.getConstraintViolations().stream()
                .map(e-> new AbstractMap.SimpleEntry<>(e.getPropertyPath().toString(), e.getMessage()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseEntity<?> handleSqlExeptions(SQLSyntaxErrorException ex){
        return new ResponseEntity<>("sqlsyntax"+ex.getMessage(),HttpStatus.BAD_REQUEST);
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSqlExeptions(SQLIntegrityConstraintViolationException ex){
        return new ResponseEntity<>("sqlintegrit"+ex.getMessage(),HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IdentifierGenerationException.class)
    public ResponseEntity<?> handleSqlExeptions(IdentifierGenerationException ex){
        return new ResponseEntity<>("iti lipseste cnp-ul",HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<?> handleSqlExeptions(PropertyValueException ex){
        return new ResponseEntity<>("propertyval"+ex.getPropertyName()+" este null",HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleSqlExeptions(HttpMessageNotReadableException ex){
        if(ex.getMessage().contains("Specializare")){
            return new ResponseEntity<>("Specializarea este gresita ",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("httpmessag"+ex.getMessage(),HttpStatus.BAD_REQUEST);
    }






  /*  @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<?> handlePatientNotFoundException(PatientNotFoundException ex){
        return new ResponseEntity<>(EntityModel.of(
                Map.of("error", ex.getMessage()),
                linkTo(methodOn(PacientController.class).getAllPacients()).withRel("parent")
        ), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<?> handleDoctorNotFoundException(DoctorNotFoundException ex){
        return new ResponseEntity<>(EntityModel.of(
                Map.of("error", ex.getMessage()),
                linkTo(methodOn(DoctorController.class).getAllDoctors()).withRel("parent")
        ), HttpStatus.NOT_FOUND);
    }*/
}
