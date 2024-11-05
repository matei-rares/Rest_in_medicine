package com.lab4.Pacient;

import com.lab4.exceptions.PacientException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PacientValidator  {

    public static void validate(Pacient pacient) {

        validateCnp(pacient.getCnp());
        validateName(pacient.getNume(),pacient.getPrenume());
        validateEmail(pacient.getEmail());
        validatePhoneNumber(pacient.getTelefon());
        validateDateOfBirth(pacient.getDataNasterii());
    }

    public static void validateCnp(String CNP) {
        if (CNP == null || CNP.isEmpty()) {
            throw new PacientException("Pacient's cnp is mandatory", HttpStatus.BAD_REQUEST);
        }

        if(CNP.length() != 13 ){
            throw new PacientException("CNP-ul trebuie să aiba 13 cifre.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Matcher matcher = Pattern.compile("^[0-9]{13}$").matcher(CNP);

        if(matcher.matches() == false){
            throw new PacientException("CNP-ul nu are formatul corect(trebuie sa aiba doar cifre).",HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }



    public static void validateDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null ) {
            throw new PacientException("Pacientul trebuie să aibă o dată de naștere.",HttpStatus.BAD_REQUEST);
        }

        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        if (birthDate.plusYears(18).isAfter(today)) {
           throw new PacientException("Pacientul trebuie să aibă cel puțin 18 ani.",HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    public static void validateName(String nume,String prenume) {
        if (nume == null || nume.isEmpty()){
            throw new PacientException("Pacientul trebuie să aibă un nume.",HttpStatus.BAD_REQUEST);
        } else if( nume.length() >50){
            throw new PacientException("Numele pacientului nu poate avea mai mult de 50 de caractere.",HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (prenume == null || prenume.isEmpty()){
            throw new PacientException("Pacientul trebuie să aibă un prenume.",HttpStatus.BAD_REQUEST);
        } else if (prenume.length() > 50) {
            throw new PacientException("Prenumele pacientului nu poate avea mai mult de 50 de caractere.",HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    public static void validateEmail(String email) {
        if(email== null || email.isEmpty()){
            throw new PacientException("Pacientul are nevoie de un email",HttpStatus.BAD_REQUEST);
        }

        if ( email.length()>70) {
            throw new PacientException("Emailul pacientului nu poate avea mai mult de 70 de caractere.",HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Matcher matcher = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        ).matcher(email);

        if(matcher.matches() == false){
            throw new PacientException("Formatul emailului e incorect.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public static void validatePhoneNumber(String telefon) {
        if(telefon==null || telefon.isEmpty()){
            throw new PacientException("Pacientul are nevoie de un nr de telefon",HttpStatus.BAD_REQUEST);
        }

        Pattern matcher = Pattern.compile( "^\\+40[0-9]{9}$"); // Numar romanesc
        Matcher m = matcher.matcher(telefon);
        if(telefon.length() != 12 || m.find() == false){
            throw new PacientException("Nr de telefon cu format gresit, trebuie sa contina +40 impreuna cu 9 cifre.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }




}