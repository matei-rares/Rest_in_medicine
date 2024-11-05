package com.lab4.Programari;

import com.lab4.Pacient.PacientDto;
import com.lab4.enums.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

import java.util.Date;

@Getter
@Setter
@Data
public class ProgramareDto {
    private Long id_user_pacient;
    private Long id_user_doctor;
    private Date data;
    private Status status;

    public ProgramareDto(Long pacient,Long doctor, Date data,Status status){
        this.id_user_pacient=pacient;
        this.id_user_doctor=doctor;
        this.data=data;
        this.status=status;
    }
    public EntityModel<ProgramareDto> toEntityModel(){
        return EntityModel.of(this);
    }


}