package com.lab4.Programari;

import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramariRepository extends JpaRepository<Programare, ProgramareID> {


        List<Programare> findAllByPacientCnp(String cnp);

        List<Programare> findAllByDoctorIdDoctor(Long id);


        List<Programare> findAllByPacientCnpOrDoctorIdDoctor(String cnp, Long doctor_idDoctor);
        List<Programare> findAllByPacientCnpAndDoctorIdDoctor(String cnp, Long doctor_idDoctor);

//     @Modifying
//     @Transactional
//     @Query("UPDATE Pacient p SET p.firstname = :field1, p.lastname = :field2 WHERE p.cnp = :cnp")
//     void updateByCnpEquals(@Param("field1") String field1, @Param("field2") String field2, @Param("cnp") long cnp);

        void deleteAllByPacientCnp(String cnp);

        }
