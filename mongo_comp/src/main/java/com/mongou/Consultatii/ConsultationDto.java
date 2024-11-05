package com.mongou.Consultatii;

import com.mongou.Investigatii.Investigation;
import com.mongou.Investigatii.InvestigationDto;
import com.mongou.enums.Diagnostic;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
public class ConsultationDto {
    private String id;
    private Long id_pacient;
    private Long id_doctor;
    private Date date;
    private Diagnostic diagnostic;
    private List<InvestigationDto> investigations;
    ConsultationDto(Consultation cons){
        this.id=cons.getId().toString();
        this.id_pacient=cons.getId_pacient();
        this.id_doctor=cons.getId_doctor();
        this.date=cons.getDate();
        this.diagnostic=cons.getDiagnostic();
        this.investigations=cons.getInvestigations().stream().map(InvestigationDto::new).toList();
    }
}
