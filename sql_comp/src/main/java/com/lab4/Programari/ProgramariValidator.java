package com.lab4.Programari;

import com.lab4.Pacient.Pacient;
import com.lab4.enums.Specializare;
import com.lab4.enums.Status;
import com.lab4.exceptions.DoctorException;
import com.lab4.exceptions.PacientException;
import com.lab4.exceptions.ProgramareException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ProgramariValidator {



    public static void validateDate(Date date) {
        if (date == null ) {
            throw new ProgramareException("Data programarii nu poate fii nula.", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime newDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime futureDateTime = currentDateTime.plusMinutes(15);


        if (newDateTime.isBefore(futureDateTime)) {
            throw new ProgramareException("Data programarii trebuie sa fie cu cel putin 15 minute fata de ora curenta.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public static void validateNullStatus(Status status) {
        if (status != null){
            throw new ProgramareException("Statusul trebuie sa fie null",HttpStatus.BAD_REQUEST);
        }

    }




}
