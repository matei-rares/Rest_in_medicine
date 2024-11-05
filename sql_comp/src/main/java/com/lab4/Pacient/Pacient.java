package com.lab4.Pacient;

import com.lab4.Programari.Programare;
import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.*;
import org.springframework.hateoas.EntityModel;

import java.util.Date;
import java.util.List;


@Entity(name="PACIENTS")
@Data
public class Pacient {

    @Id
    @Column(name = "cnp",length = 13) // SQL EXCEPTION
    private String cnp;

    @Column(name = "id_user", unique = true ,nullable=false)
    private Long idUser;

    @Column(name = "nume", length = 50, nullable = false)
    private String nume;

    @Column(name = "prenume", length = 50,nullable = false)
    private String prenume;

    @Column(name = "email", length = 70, nullable = false,unique = true)
    //@Email(message = "Pacientul are un email invalid")
    private String email;

    @Column(name = "telefon", length =13, nullable = false)
    private String telefon;


    @Column(name = "data_nasterii", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataNasterii; // daca face ceva de genu 2021-13-01, nu am ce se ii fac deci se verifica din front end

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "pacient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Programare> programari;


    public Pacient(String cnp, Long idUser, String nume, String prenume, String email, String telefon, Date dataNasterii, Boolean isActive) {
        this.cnp = cnp;
        this.idUser = idUser;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.dataNasterii = dataNasterii;
        this.isActive = isActive;
    }


    public Pacient() {

    }
    public PacientDto toDto() {
        return new PacientDto(this.getCnp(), this.getIdUser(), this.getNume(), this.getPrenume(),this.getEmail(),this.getTelefon(),this.getDataNasterii(),this.getIsActive());
    }

    public EntityModel<PacientDto> toEntityModelDto(){
        return this.toDto().toEntityModel();
    }


}
