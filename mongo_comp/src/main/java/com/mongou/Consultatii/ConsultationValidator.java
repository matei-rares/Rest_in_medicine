package com.mongou.Consultatii;

import com.mongou.Investigatii.Investigation;
import com.mongou.Investigatii.InvestigationDto;
import com.mongou.enums.Diagnostic;
import com.mongou.execeptions.ConsultationException;
import com.mongou.execeptions.CustomException;
import org.springframework.http.HttpStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class  ConsultationValidator{

    public static void validate(ConsultationDto cons) {
        //nu am ce sa validez la id pt ca se genereaza automat, verific in service daca exista deja
        validateId(cons.getId_pacient());
        validateId(cons.getId_doctor());
    }


    public static void validateInvestigation(InvestigationDto investig){
        if(investig == null){
            throw new ConsultationException("Investigatia nu poate fi nula.",BAD_REQUEST);
        }
        validateString(investig.getDenumire(),"Numele investigatiei");
        validateString(investig.getRezultat(),"Rezultatul investigatiei");
        validateString(investig.getDurata_de_procesare(),"Durata de procesare a investigatiei");
    }
    public static void validateObjectid(String id){
        if(id == null || id.isEmpty()){
            throw new ConsultationException("Id-ul nu poate fi gol.",BAD_REQUEST);
        }
    }
    public static void validateDiagnostic(Diagnostic diagnostic){
        if(diagnostic == null){
            throw new ConsultationException("Diagnosticul nu poate fi nul.",BAD_REQUEST);
        }
        try{
            diagnostic=Diagnostic.valueOf(diagnostic.toString().toUpperCase());
        }
        catch (Exception e){
            throw new CustomException("Diagnostic " + diagnostic + " nu exista",HttpStatus.NOT_FOUND);
        }
    }


    private static void validateId(Long id){
        if(id == null || id < 0){
            throw new ConsultationException("Id-ul nu poate fi negativ.",BAD_REQUEST);
        }
    }

    public static void validateString(String caractere,String nume) {
        if (caractere == null || caractere.isEmpty()){
            throw new ConsultationException(nume+" nu trebuie sa fie gol.",BAD_REQUEST);
        } else if( caractere.length() >300){
            throw new ConsultationException(nume+" nu poate avea mai mult de 50 de caractere.",UNPROCESSABLE_ENTITY);
        }


    }




}
