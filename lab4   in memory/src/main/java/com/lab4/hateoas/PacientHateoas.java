package com.lab4.hateoas;

import com.lab4.controller.PacientController;
import com.lab4.dto.PacientDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PacientHateoas implements RepresentationModelAssembler<PacientDto, EntityModel<PacientDto>> {
    @Override
    public EntityModel<PacientDto> toModel(PacientDto pacient) {
       EntityModel<PacientDto> pacientModel = EntityModel.of(pacient,
               linkTo(methodOn(PacientController.class).getPacientById(pacient.getId())).withSelfRel(),
               linkTo(methodOn(PacientController.class).getAllPacients()).withRel("parent")
               );
       return pacientModel;
    }


}
