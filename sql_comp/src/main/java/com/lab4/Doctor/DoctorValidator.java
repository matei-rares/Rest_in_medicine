package com.lab4.Doctor;


import com.lab4.exceptions.DoctorException;
import org.springframework.http.HttpStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


public class  DoctorValidator  {

    public static void validate(Doctor doctor) {
        //nu am ce sa validez la id pt ca se genereaza automat, verific in service daca exista deja
        validateName(doctor.getNume(),doctor.getPrenume());
        validateEmail(doctor.getEmail());
        validatePhoneNumber(doctor.getTelefon());
        validateId(doctor.getIdUser());
    }


    public static void validateName(String nume,String prenume) {
        if (nume == null || nume.isEmpty()){
            throw new DoctorException("Doctorul trebuie să aibă un nume.",BAD_REQUEST);
        } else if( nume.length() >50){
            throw new DoctorException("Numele doctorului nu poate avea mai mult de 50 de caractere.",UNPROCESSABLE_ENTITY);
        }

        if (prenume == null || prenume.isEmpty()){
            throw new DoctorException("Doctorul trebuie să aibă un prenume.",BAD_REQUEST);
        } else if (prenume.length() > 50) {
            throw new DoctorException("Doctorul pacientului nu poate avea mai mult de 50 de caractere.",UNPROCESSABLE_ENTITY);
        }

    }

    private static void validateEmail(String email) {
        if(email== null || email.isEmpty()){
            throw new DoctorException("Doctorul are nevoie de un email", HttpStatus.BAD_REQUEST);
        }
        if ( email.length()>70) {
            throw new DoctorException("Emailul doctorului nu poate avea mai mult de 70 de caractere.",UNPROCESSABLE_ENTITY);
        }
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()==false){
            throw new DoctorException("Emailul doctorului nu este valid.",UNPROCESSABLE_ENTITY);
        }
    }

    private static void validatePhoneNumber(String telefon) {
        if(telefon==null || telefon.isEmpty()){
            throw new DoctorException("Doctor are nevoie de un nr de telefon",HttpStatus.BAD_REQUEST);
        }

        Pattern matcher = Pattern.compile( "^\\+40[0-9]{9}$"); // Numar romanesc
        Matcher m = matcher.matcher(telefon);
        if(telefon.length() != 12 || m.find() == false){
            throw new DoctorException("Nr de telefon cu format gresit, trebuie sa contina +40 impreuna cu 9 cifre.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static void validateId(Long id) {
        if(id == null || id < 0){
            throw new DoctorException("Id-ul nu poate fi negativ sau inexistent.",BAD_REQUEST);
        }

    }



}
