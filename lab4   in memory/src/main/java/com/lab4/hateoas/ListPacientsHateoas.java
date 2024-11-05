package com.lab4.hateoas;

import com.lab4.dto.PacientDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;

import com.lab4.controller.PacientController;
import com.lab4.dto.PacientDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ListPacientsHateoas implements RepresentationModelAssembler<List<PacientDto>, CollectionModel<EntityModel<PacientDto>>> {
    @Override
    public CollectionModel<EntityModel<PacientDto>> toModel(List<PacientDto> entities) {
        List<EntityModel<PacientDto>> pacientModels = entities.stream()
                .map(pacient -> EntityModel.of(pacient,
                        linkTo(methodOn(PacientController.class).getPacientById(pacient.getId())).withSelfRel(),
                        linkTo(methodOn(PacientController.class).getAllPacients()).withRel("parent")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(pacientModels, linkTo(methodOn(PacientController.class).getAllPacients()).withSelfRel());
    }
}