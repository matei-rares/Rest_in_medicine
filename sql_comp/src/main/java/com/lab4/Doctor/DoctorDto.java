package com.lab4.Doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lab4.Pacient.Pacient;
import com.lab4.Pacient.PacientDto;
import com.lab4.enums.Specializare;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

import java.util.Date;

@Getter
@Setter
@Data
public class DoctorDto {
    private Long id_doctor;
    private Long idUser;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private Specializare specializare;

    public DoctorDto(Long id_doctor, Long idUser, String nume, String prenume, String email, String telefon, Specializare specializare) {
        this.id_doctor = id_doctor;
        this.idUser = idUser;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.specializare=specializare;
    }

    public EntityModel<DoctorDto> toEntityModel(){
        return EntityModel.of(this);
    }
    public Doctor toDoctor() {
        return new Doctor(this.getId_doctor(),this.getIdUser(),this.getNume(),this.getPrenume(),this.getEmail(),this.getTelefon(),this.getSpecializare());
    }
}
