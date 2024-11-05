package com.lab4.Doctor;

import com.lab4.Pacient.PacientDto;
import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;
import com.lab4.enums.Status;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Optional;

public interface IDoctor {
    Doctor findById(Long id);
    Doctor findByUserId(Long id);
    Doctor deleteById(Long id);

    Doctor deleteByUserId(Long id);

    List<DoctorDto> findAllBySpecializare(String spec);

    List<DoctorDto> findAllByName(String s);

    List<DoctorDto> findAll();

    Doctor save(Doctor entity);
    List<PacientDto> findOwnPacientsByUserId(Long user_id);

    List<ProgramareDto> findAllAppointmentsOfPacient(Long doc_id, Long pac_id);

    Programare createAppointmentByUserId(Long id, Programare prog1);

    List<ProgramareDto> findAllAppointmentsByUserId(Long id);

     CollectionModel<EntityModel<DoctorDto>> getAllDoctorsByPageParams(Optional<Integer> page, Optional<Integer> items_per_page);
     ProgramareDto findAppByDocIdPacIdAndDate(Long doc_id, Long pac_id, String date);
     Programare modifyApp(Long doc_id, Long pac_id, String date, String statusParam);

     void deleteApp(Long id,Long doc_id, Long pac_id, String date);

    }
