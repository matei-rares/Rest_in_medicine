package com.lab4.Pacient;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

import java.util.Date;

@Getter
@Setter
@Data
public class UpdatePacientDto {
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private Date dataNasterii;
    private Boolean isActive;

    public UpdatePacientDto( String nume, String prenume, String email, String telefon, Date dataNasterii, Boolean isActive) {

        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.dataNasterii = dataNasterii;
        this.isActive = isActive;
    }


    public EntityModel<UpdatePacientDto> toEntityModel(){
        return EntityModel.of(this);
    }

}
