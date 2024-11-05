package com.lab4.Pacient;

import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;

import java.util.List;
import java.util.Optional;

public interface IPacient {
    Pacient save(Pacient pacient);
     Pacient saveOrUpdate(Pacient pacient);

    List<Pacient> findAll();

    Pacient findByUserId(Long id);

    Pacient deleteByUserId(Long userid);

    Long getUserIdBasedOnCnp(String Cnp);

    List<ProgramareDto> findAllAppointmentsByCnp(String cnp);

    Programare createAppointmentByUserId(Long id, Programare prog1);

    List<ProgramareDto> getAllPacientAppointmentsWithParams(Long user_id, Optional<String> dateParam, Optional<String> typeParam);

     Pacient updatePacientById(Long id, UpdatePacientDto pacient);

     Pacient replacePacientData(Long uid,PacientDto pac);
    }