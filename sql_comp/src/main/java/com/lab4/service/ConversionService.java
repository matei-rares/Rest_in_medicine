package com.lab4.service;

import com.lab4.Doctor.DoctorDto;
import com.lab4.Doctor.DoctorRepository;
import com.lab4.Pacient.PacientDto;
import com.lab4.Pacient.PacientRepository;
import com.lab4.Programari.ProgramareDto;
import com.lab4.Doctor.Doctor;
import com.lab4.Pacient.Pacient;
import com.lab4.Programari.Programare;
import com.lab4.exceptions.PacientSqlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConversionService {


    @Autowired
    private final PacientRepository pacientRepository;
    @Autowired
    private final DoctorRepository doctorRepository;

    public ConversionService(PacientRepository pacientRepository, DoctorRepository doctorRepository) {
        this.pacientRepository = pacientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Pacient toEntity(PacientDto dto) {
       return new Pacient(dto.getCnp(),dto.getIdUser(),dto.getNume(),dto.getPrenume(),dto.getEmail(),dto.getTelefon(),dto.getDataNasterii(),dto.getIsActive());
    }


    public DoctorDto toDto(Doctor entity) {
        return new DoctorDto(entity.getIdDoctor(), entity.getIdUser(), entity.getNume(), entity.getPrenume(),entity.getEmail(),entity.getTelefon(),entity.getSpecializare());
    }

    public Doctor toEntity(DoctorDto dto) {
        return new Doctor(dto.getId_doctor(),dto.getIdUser(),dto.getNume(),dto.getPrenume(),dto.getEmail(),dto.getTelefon(),dto.getSpecializare());
    }


    public ProgramareDto toDto(Programare entity) {
        String cnp_pacient = entity.getId_pacient();
        Long doctorId = entity.getId_doctor();

        Optional<Pacient> result = pacientRepository.findByCnp(cnp_pacient);
        Long id_pacient=0L;
        if(result.isPresent())
            id_pacient=result.get().getIdUser();
        else
            throw new PacientSqlException("Pacientul cu cnp " + cnp_pacient + " nu exista in baza de date", HttpStatus.NOT_FOUND);

        Optional<Doctor>  result2 = doctorRepository.findById(doctorId);
        Long id_doctor= 0L;

        if(result2.isPresent()){
            id_doctor=result2.get().getIdUser();
        }
        else{
            throw new PacientSqlException("Doctorul cu id-ul " + doctorId + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }
        return new ProgramareDto(id_pacient,id_doctor,entity.getDate(),entity.getStatus());
    }

    public Programare toEntity(ProgramareDto dto) {
        Long pacient_id = dto.getId_user_pacient();
        Long doctor_id = dto.getId_user_doctor();

        Optional<Pacient> result = pacientRepository.findByIdUser(pacient_id);
        String pacient_cnp="";
        if(result.isPresent())
            pacient_cnp=result.get().getCnp();
        else
            throw new PacientSqlException("Pacientul cu user id-ul " + pacient_id + " nu exista in baza de date", HttpStatus.NOT_FOUND);

        Optional<Doctor>  result2 = doctorRepository.findByIdUser(doctor_id);
        Long id_doctor= 0L;

        if(result2.isPresent()){
            id_doctor=result2.get().getIdDoctor();
        }
        else{
            throw new PacientSqlException("Doctorul cu id-ul " + doctor_id + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }

        return new Programare(pacient_cnp,id_doctor,dto.getData(),dto.getStatus());
    }
}