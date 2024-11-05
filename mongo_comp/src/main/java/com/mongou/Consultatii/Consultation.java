package com.mongou.Consultatii;


import com.mongou.Investigatii.Investigation;
import com.mongou.enums.Diagnostic;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.hateoas.EntityModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "CONSULTATIONS")
public class Consultation {
    @Id
    private String id;

    private Long id_pacient;

    private Long id_doctor;
    private Date date;
    private Diagnostic diagnostic;
    private List<Investigation> investigations;

    public Consultation(ConsultationDto dto)
    {
        this.id=new ObjectId().toString();
        this.id_pacient=dto.getId_pacient();
        this.id_doctor=dto.getId_doctor();
        this.diagnostic=dto.getDiagnostic();
        this.date=dto.getDate();
        this.investigations=new ArrayList<>();
    }

    public EntityModel<ConsultationDto> toEntityModelDto(){
        return EntityModel.of(new ConsultationDto(this));
    }

}
