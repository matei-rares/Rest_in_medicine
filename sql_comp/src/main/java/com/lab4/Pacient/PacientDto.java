package com.lab4.Pacient;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.EntityModel;

import java.util.Date;


@Getter
@Setter
@Data
public class PacientDto {
    private String cnp;
    private Long idUser;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private Date dataNasterii;
    private Boolean isActive;

    public PacientDto(String cnp, Long idUser, String nume, String prenume, String email, String telefon, Date dataNasterii, Boolean isActive) {
        this.cnp = cnp;
        this.idUser = idUser;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.dataNasterii = dataNasterii;
        this.isActive = isActive;
    }


    public EntityModel<PacientDto> toEntityModel(){
        return EntityModel.of(this);
    }
    public Pacient toPacient() {
        return new Pacient(this.getCnp(),this.getIdUser(),this.getNume(),this.getPrenume(),this.getEmail(),this.getTelefon(),this.getDataNasterii(),this.getIsActive());
    }
}


