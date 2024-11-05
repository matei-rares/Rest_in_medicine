package com.lab4.Doctor;

import com.lab4.Pacient.Pacient;
import com.lab4.enums.Specializare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {


        List<Doctor> findAll() ;

        Optional<Doctor> findById(Long id_doctor) ;
        Optional<Doctor> findByIdUser(Long id_user) ;
        List<Doctor> getDoctorsBySpecializare(Specializare spec);

        List<Doctor> findAllByNumeContaining(String nume);

        Doctor save(Doctor pacient);

        void deleteById(Long id_doctor);

        void deleteByIdUser(Long id_user);


        Optional<Doctor> findByEmail(String email) ;


}

//     @Modifying
//     @Transactional
//     @Query("UPDATE Pacient p SET p.firstname = :field1, p.lastname = :field2 WHERE p.cnp = :cnp")
//     void updateByCnpEquals(@Param("field1") String field1, @Param("field2") String field2, @Param("cnp") long cnp);

