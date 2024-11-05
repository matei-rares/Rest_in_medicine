package com.lab4.Programari;

import com.lab4.Doctor.Doctor;
import com.lab4.enums.Status;
import com.lab4.Pacient.Pacient;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "PROGRAMARI")
@IdClass(ProgramareID.class)
@Data
public class Programare {
    @Id
    @Column(name="cnp")
    private String id_pacient;
    @Id
    @Column(name="id_doctor")
    private Long id_doctor;
    @Id
    @Column(name = "data_programare")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "cnp", referencedColumnName = "cnp", insertable = false, updatable = false)
    private Pacient pacient;

    @ManyToOne
    @JoinColumn(name = "id_doctor", referencedColumnName = "id_doctor", insertable = false, updatable = false)
    private Doctor doctor;

    public Programare(String cnp,Long idDoctor, Date data,Status status){
        this.id_pacient=cnp;
        this.id_doctor=idDoctor;
        this.date=data;
        this.status=status;
    }

    public Programare() {

    }
}
