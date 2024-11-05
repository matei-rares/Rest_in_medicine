package com.mongou.Investigatii;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "INVESTIGATIONS")
public class Investigation {
    @Id
    private String id;
    private String denumire;
    private String durata_de_procesare;//nr zile
    private String rezultat;

    public Investigation(InvestigationDto dto)
    {
        this.id=new ObjectId().toString();
        this.denumire=dto.getDenumire();
        this.durata_de_procesare=dto.getDurata_de_procesare();
        this.rezultat=dto.getRezultat();
    }


 /*   public Investigation(String denumire, String durata_de_procesare, String rezultat)
    {
        super();
        this.denumire = denumire;
        this.durata_de_procesare = durata_de_procesare;
        this.rezultat = rezultat;
    }*/

}
