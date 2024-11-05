package com.lab4.Pacient;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PacientHateoas implements RepresentationModelAssembler<PacientDto, EntityModel<PacientDto>> {


    @Override
    public EntityModel<PacientDto> toModel(PacientDto pacient) {

        EntityModel<PacientDto> pacientModel = EntityModel.of(pacient,
                linkTo(methodOn(PacientController.class).getPacientById(pacient.getIdUser())).withSelfRel(),
                linkTo(methodOn(PacientController.class).getAllPacients()).withRel("parent")
        );
        return pacientModel;
    }

    @Override
    public CollectionModel<EntityModel<PacientDto>> toCollectionModel(Iterable<? extends PacientDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities).add(linkTo(methodOn(PacientController.class).getAllPacients()).withSelfRel().withType("GET"));
    }


}
