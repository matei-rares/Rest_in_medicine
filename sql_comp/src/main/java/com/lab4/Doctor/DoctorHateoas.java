package com.lab4.Doctor;

import com.lab4.Doctor.DoctorController;
import com.lab4.Doctor.DoctorDto;
import com.lab4.enums.Specializare;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DoctorHateoas implements RepresentationModelAssembler<DoctorDto, EntityModel<DoctorDto>> {


    @Override
    public EntityModel<DoctorDto> toModel(DoctorDto doctor) {
         Optional<String> spec=null;
        EntityModel<DoctorDto> pacientModel = EntityModel.of(doctor,
                linkTo(methodOn(DoctorController.class).getDoctorById(doctor.getId_doctor())).withSelfRel(),
                linkTo(methodOn(DoctorController.class).getAllDoctors(null,null,null,null)).withRel("parent")
        );
        return pacientModel;
    }

    @Override
    public CollectionModel<EntityModel<DoctorDto>> toCollectionModel(Iterable<? extends DoctorDto> entities) {
        Optional<String> spec=null;
        return RepresentationModelAssembler.super.toCollectionModel(entities).add(linkTo(methodOn(DoctorController.class).getAllDoctors(null,null,null,null)).withSelfRel());
    }
}