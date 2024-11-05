package com.lab4.Doctor;

import com.lab4.Pacient.PacientDto;
import com.lab4.enums.Specializare;
import com.lab4.Programari.Programare;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

import java.util.List;

@Entity
@Table(name="DOCTORS")
@Getter
@Setter
public class Doctor {
    @Id
    @Column(name = "id_doctor")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoctor;

    @Column(name = "id_user", unique = true,nullable = false)
    private Long idUser;

    @Column(name = "nume", length = 50, nullable = false)
    private String nume;

    @Column(name = "prenume", length = 50,nullable = false)
    private String prenume;

    @Column(name = "email", length = 70, nullable = false, unique = true)
    @Email(message = "Doctorul are un email invalid")
    private String email;

    @Column(name = "telefon", length =12, nullable = false)
    private String telefon;

    @Column(name = "specializare")
    private Specializare specializare;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Programare> programari;

    public Doctor(Long id, Long idUser, String nume, String prenume, String email, String telefon, Specializare specializare) {
        this.idDoctor = id;
        this.idUser = idUser;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.specializare = specializare;
    }

    public Doctor() {}

    public DoctorDto toDto() {
        return new DoctorDto(this.getIdDoctor(), this.getIdUser(), this.getNume(), this.getPrenume(),this.getEmail(),this.getTelefon(),this.getSpecializare());
    }

    public EntityModel<DoctorDto> toEntityModelDto(){
        return this.toDto().toEntityModel();
    }


}

