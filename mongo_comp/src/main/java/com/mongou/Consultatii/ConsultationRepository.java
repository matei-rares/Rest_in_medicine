package com.mongou.Consultatii;

import com.mongou.Investigatii.Investigation;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends MongoRepository<Consultation,String> {

    Consultation findById(ObjectId id);

    @Query("{ 'id_doctor' : ?0 }")
    List<Consultation> findAllByDoctorId(Long id_doctor);

    @Query("{ 'id_pacient' : ?0 }")
    List<Consultation> findAllByPacientId(Long id_pacient);

    @Query("{ 'id_pacient' : ?0, 'id_doctor' : ?1 }")
    List<Consultation> findAllByPacientAndDoctor(Long id_pacient, Long id_doctor);

}
