package com.lab4.Doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorPageRepository   extends PagingAndSortingRepository<Doctor, Long> {

    Page<Doctor> findAll(Pageable pageable);


}
