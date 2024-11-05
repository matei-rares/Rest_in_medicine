package com.lab4.Pacient;

import com.lab4.Pacient.Pacient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PacientRepository extends JpaRepository<Pacient,String> {


     List<Pacient> findAll() ;

     Optional<Pacient> findByCnp(String cnp);

     Optional<Pacient> findByIdUser(Long idUser);

     void deleteByIdUser(Long idUser);
     Optional<Pacient> findByEmail(String email) ;

     Pacient save(Pacient pacient);

}
