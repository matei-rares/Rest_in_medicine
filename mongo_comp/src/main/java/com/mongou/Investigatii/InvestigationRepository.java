package com.mongou.Investigatii;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestigationRepository extends MongoRepository<Investigation,String> {

    public Investigation save(Investigation investigation);
    public Optional<Investigation> findById(String id);

    //public Investigation findByName(String name);
}
