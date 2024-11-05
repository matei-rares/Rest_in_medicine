package com.mongou.Investigatii;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class InvestigationDto {
   private String id;
    private String denumire;
    private String durata_de_procesare;//nr zile
    private String rezultat;

    public InvestigationDto(String id, String denumire, String durata_de_procesare, String rezultat)
    {
        this.id=id;
        this.denumire = denumire;
        this.durata_de_procesare = durata_de_procesare;
        this.rezultat = rezultat;
    }

    public InvestigationDto(Investigation invest)
    {
        this.id=invest.getId().toString();
        this.denumire = invest.getDenumire();
        this.durata_de_procesare = invest.getDurata_de_procesare();
        this.rezultat = invest.getRezultat();
    }

}