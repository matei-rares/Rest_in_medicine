package com.lab4.Programari;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.IdClass;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProgramareID implements Serializable {
    private String id_pacient;
    private Long id_doctor;
    private Date date;
}
