package com.lab4.controller;

import com.lab4.dto.PacientDto;
import com.lab4.hateoas.ListPacientsHateoas;
import com.lab4.hateoas.PacientHateoas;
import com.lab4.repository.PacientRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.hal.CollectionModelMixin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/medical_office/pacients")
public class PacientController {

    @Autowired
    private PacientRepository pacientRepository;

    @GetMapping("/")
    public ResponseEntity<?> getAllPacients(){
        List<PacientDto> pacients= pacientRepository.findAll();

        return new ResponseEntity<>( new ListPacientsHateoas().toModel(pacients),HttpStatus.OK);//new ListPacientsHateoas().toCollectionModel(pacients.stream().iterator()) , HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient found",
                    content = {@Content(mediaType="application/json",schema=@Schema(implementation= PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
    public ResponseEntity<?> getPacientById(@PathVariable long id){
        Optional<PacientDto> pacient= pacientRepository.findById(id);

        if(pacient.isPresent()){
            return new ResponseEntity<>(new PacientHateoas().toModel(pacient.get()), HttpStatus.OK);

        }
        else{//Nu s-a gasit pacient returnez parent
            return new ResponseEntity<>(returnParent(), HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping("/")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "201",
                    description = "Pacient created successfully",
                    content = {@Content(mediaType="application/json",schema=@Schema(implementation= PacientDto.class))}
            )
    })
    public ResponseEntity<?> createPacient(@RequestBody PacientDto pacient){
        PacientDto savedpacient= pacientRepository.save(pacient);
        return new ResponseEntity<>(new PacientHateoas().toModel(savedpacient), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient edited",
                    content = {@Content(mediaType="application/json",schema=@Schema(implementation= PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
    public ResponseEntity<?> editPacient(@PathVariable Long id, @RequestBody PacientDto newPacient){
        //PacientDto savedpacient= pacientRepository.save(pacient);

        Optional<PacientDto> pacient= pacientRepository.findById(id);

        if(pacient.isPresent()){
            PacientDto savedpacient= pacient.get();

            if(newPacient.getAge() != 0){
                savedpacient.setAge(newPacient.getAge());
            }
            if(newPacient.getFirstname() != ""){
                savedpacient.setFirstname(newPacient.getFirstname());
            }
            if(newPacient.getLastname() != ""){
                savedpacient.setLastname(newPacient.getLastname());
            }
            pacientRepository.replace(id,savedpacient);

            return new ResponseEntity<>(new PacientHateoas().toModel(savedpacient), HttpStatus.OK);

        }
        else{//Nu s-a gasit pacient returnez parent
            return new ResponseEntity<>(returnParent(), HttpStatus.NOT_FOUND);

        }

    }

    @DeleteMapping("/{id}")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "204",
                    description = "Pacient found",
                    content = {@Content(mediaType="application/json",schema=@Schema(implementation= PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
    public ResponseEntity<?> deletePacient(@PathVariable long id){
        if(pacientRepository.findById(id).isPresent()){
                pacientRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(returnParent(), HttpStatus.NOT_FOUND);
        }

    }


    public Map<String, ArrayList<Link>> returnParent(){
        Map<String, ArrayList<Link>> links = new HashMap<>();
        ArrayList<Link> arrayLinks =new ArrayList<>();
        Link parentLink =linkTo(methodOn(PacientController.class).getAllPacients()).withRel("parent");
        arrayLinks.add(parentLink);
        links.put("_links", new ArrayList<Link>(arrayLinks));
        return links;
    }

}
