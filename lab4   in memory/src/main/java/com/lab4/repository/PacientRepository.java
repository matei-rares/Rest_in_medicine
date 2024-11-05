package com.lab4.repository;

import com.lab4.dto.PacientDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PacientRepository {
    private List<PacientDto> pacients=new ArrayList<>();
    private long nextId=1;

    public List<PacientDto> findAll() {return pacients;}

    public Optional<PacientDto> findById(long id) {
        return pacients.stream()
                .filter(student->student.getId() == (id))
                .findFirst();
    }

    public PacientDto save(PacientDto student){
        student.setId(nextId++);
        pacients.add(student);
        return student;
    }
    public Boolean replace(long id, PacientDto newStudent){
        pacients.stream()
                .filter(student->student.getId() == (id))
                .findFirst().map(x-> {
                    x.setFirstname(newStudent.getFirstname());
                    x.setLastname(newStudent.getLastname());
                    x.setAge(newStudent.getAge());
                    return x;
                });

        // xsetFirstname(newStudent.getFirstname())); : "mata"
        return true;
    }

    public void deleteById(long id) {
        pacients.removeIf(student->student.getId() == (id)) ;
    }

}
