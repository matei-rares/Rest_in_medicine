package com.lab4.Pacient;

import com.lab4.Config.HeaderFilter;
import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;
import com.lab4.service.ConversionService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "Authorization")
@RequestMapping("/api/medical_office/pacients")
public class PacientController extends ResponseEntityExceptionHandler {
    @Autowired
    private IPacient pacientService;
    @Autowired
    private ConversionService conversionService;
    private final PacientController THIS_CLASS = methodOn(PacientController.class);

    @Operation(summary = "Get all the pacients from database", responses = {@ApiResponse(
            responseCode = "200",
            description = "Pacients found"
    ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to create pacients"
            )
    })
    @GetMapping("/")
    public ResponseEntity<?> getAllPacients() {
        if(!HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not authorized to access this resource", HttpStatus.FORBIDDEN);
        }


        List<Pacient> pacients = pacientService.findAll();

        if (pacients.isEmpty()) {
            CollectionModel<EntityModel<PacientDto>> ar1 = pacientsToCollectionModel(pacients)
                    .add(linkTo(THIS_CLASS.getAllPacients()).withSelfRel().withType("GET"))
                    .add(linkTo(THIS_CLASS.createPacient(null)).withRel("createPacient").withType("PUT"));
            return new ResponseEntity<>(ar1, HttpStatus.OK);
        }
        CollectionModel<EntityModel<PacientDto>> ar1 = pacientsToCollectionModel(pacients)
                .add(linkTo(THIS_CLASS.getAllPacients()).withSelfRel().withType("GET"));
        return new ResponseEntity<>(ar1, HttpStatus.OK);
    }


    @Operation(summary = "Get details of a pacient", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to create pacients"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
    @GetMapping(value = "/{id}") //Asta e user id
    public ResponseEntity<?> getPacientById(@PathVariable Long id) {
        if( (HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(id.toString()) || HeaderFilter.getCurrentRole().equals("DOCTOR"))){
            return new ResponseEntity<>("You are not authorized to access this resource", HttpStatus.FORBIDDEN);
        }

        Pacient pac = pacientService.findByUserId(id);

        EntityModel<PacientDto> result = pac.toEntityModelDto()
                .add(linkTo(THIS_CLASS.getPacientById(id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getAllPacientAppointments(null, null, pac.getIdUser())).withRel("appointments").withType("GET"))
                .add(linkTo(THIS_CLASS.createPacientAppointment(id, null)).withRel("createAppointment").withType("POST"))
                .add(linkTo(THIS_CLASS.getAllPacients()).withRel("parent").withType("GET"));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Operation(summary = "Create a pacient", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pacient created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A field is null or empty"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to create pacients"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Current pacient is in conflict with database"
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "Should be application/json"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid pacient field syntax"
            )
    })

    @PutMapping("/") //Put pentru ca primim id-ul de la client
    public ResponseEntity<?> createPacient(
            @ApiParam(value = "Details of pacient", required = true)
            @Valid @RequestBody PacientDto pacient) {
        if(HeaderFilter.getCurrentRole().equals("DOCTOR") ||( HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(pacient.getIdUser().toString()))){
            return new ResponseEntity<>("You are not allowed to make this operation", HttpStatus.FORBIDDEN);
        }

        Pacient pac = new Pacient(pacient.getCnp(), pacient.getIdUser(), pacient.getNume(), pacient.getPrenume(), pacient.getEmail(), pacient.getTelefon(), pacient.getDataNasterii(), pacient.getIsActive());
        EntityModel<PacientDto> rasp = pacientService.saveOrUpdate(pac).toEntityModelDto()
                .add(linkTo(THIS_CLASS.createPacient(null)).withSelfRel().withType("PUT"))
                .add(linkTo(THIS_CLASS.createPacientAppointment(null, null)).withRel("create_appointment").withType("POST"))
                .add(linkTo(THIS_CLASS.getAllPacients()).withRel("parent").withType("GET"))
                .add(linkTo(THIS_CLASS.getPacientById(pacient.getIdUser())).withRel("get_pacient").withType("GET"));

        return new ResponseEntity<>(rasp, HttpStatus.CREATED);

    }


    @Operation(summary = "Delete a pacient", responses = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Pacient deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to delete pacients"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePacient(@PathVariable Long id) {
        if(!HeaderFilter.getCurrentRole().equals("ADMIN")  ){
            return new ResponseEntity<>("You are not allowed to make this operation", HttpStatus.FORBIDDEN);
        }
        pacientService.deleteByUserId(id);

        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    ////////////////////////////////APPOINTMENTS///////////////////////////////////////////////
    @Operation(summary = "Get all appointments of a pacient", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProgramareDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Request parameters are invalid"
            )
    })
    @GetMapping("/{id}/appointments")
    public ResponseEntity<?> getAllPacientAppointments(
            @RequestParam(name = "date", required = false) Optional<String> dateParam,
            @RequestParam(name = "type", required = false) Optional<String> typeParam,
            @PathVariable Long id) {
        if( (HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(id.toString())) || HeaderFilter.getCurrentRole().equals("DOCTOR") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to access this resource", HttpStatus.FORBIDDEN);
        }

        List<ProgramareDto> a = pacientService.getAllPacientAppointmentsWithParams(id, dateParam, typeParam);

        if (a.isEmpty()) {
            CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                    .add(linkTo(THIS_CLASS.getAllPacientAppointments(dateParam, typeParam, id)).withSelfRel().withType("GET"))
                    .add(linkTo(THIS_CLASS.getPacientById(id)).withRel("parent").withType("GET"))
                    .add(linkTo(THIS_CLASS.createPacientAppointment(id,null)).withRel("createPacient").withType("POST"));
            return new ResponseEntity<>(ar1, HttpStatus.OK);
        }
        CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                .add(linkTo(THIS_CLASS.getAllPacientAppointments(dateParam, typeParam, id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getPacientById(id)).withRel("parent").withType("GET"));
        return new ResponseEntity<>(ar1, HttpStatus.OK);

    }

    @Operation(summary = "Add an appointment to a pacient", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment added",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProgramareDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A field is null or empty"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to create appointments"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient/Doctor not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Current appointment is in conflict with database or with path variable"

            )
    })
    @PostMapping("/{id}/appointments")
    public ResponseEntity<?> createPacientAppointment(@PathVariable Long id, @RequestBody ProgramareDto prog) {
        if(( HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(id.toString())) || HeaderFilter.getCurrentRole().equals("DOCTOR") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to make this operation ", HttpStatus.FORBIDDEN);
        }

        System.out.println(prog.getData().toString());
        Programare prog1 = conversionService.toEntity(prog);
        Programare res = pacientService.createAppointmentByUserId(id, prog1);
        ProgramareDto res1 = conversionService.toDto(res);


        EntityModel<ProgramareDto> ent = EntityModel.of(res1)
                .add(linkTo(THIS_CLASS.createPacientAppointment(id, null)).withSelfRel().withType("POST"))
                .add(linkTo(THIS_CLASS.getAllPacientAppointments(null,null, id)).withRel("getAllAppointments").withType("GET"))
                .add(linkTo(THIS_CLASS.getPacientById(id)).withRel("parent").withType("GET"));
        return new ResponseEntity<>(ent, HttpStatus.CREATED);
    }


    @Operation(summary = "Update a pacient", responses = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A field is null or empty"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to update pacient"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
            //todo copiat toate raspunsurile de la creare user
    })
    @PatchMapping("/update/{uid}")
    public ResponseEntity<?> update(@PathVariable Long uid, @RequestBody PacientDto pacient) {
        if( (HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(uid.toString()))  || HeaderFilter.getCurrentRole().equals("DOCTOR") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not authorized to make this operation", HttpStatus.FORBIDDEN);
        }


        Pacient a=pacientService.replacePacientData(uid, pacient);
        EntityModel<PacientDto> b= a.toEntityModelDto()
                .add(linkTo(THIS_CLASS.update(uid,pacient)).withSelfRel().withType("PUT"))
                .add(linkTo(THIS_CLASS.getPacientById(uid)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(b, HttpStatus.OK);
    }



    @Operation(summary = "Modify the state of a pacient", responses = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient's state changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A field is null or empty"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to perform this operation"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
            //todo copiat toate raspunsurile de la creare user
    })
    @PatchMapping("/state/{uid}")
    public ResponseEntity<?> changeState(@PathVariable Long uid, @RequestBody(required = false) UpdatePacientDto pacientState) {
        if( (HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(uid.toString()))  || HeaderFilter.getCurrentRole().equals("DOCTOR")){
            return new ResponseEntity<>("You are not make this operation", HttpStatus.FORBIDDEN);
        }

        Pacient a=pacientService.updatePacientById(uid, pacientState);
        EntityModel<PacientDto> b= a.toEntityModelDto()
                .add(linkTo(THIS_CLASS.changeState(uid,pacientState)).withSelfRel().withType("PATCH"))
                .add(linkTo(THIS_CLASS.getPacientById(uid)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(b, HttpStatus.OK);
    }



    private CollectionModel<EntityModel<ProgramareDto>> programariToCollectionModel(List<ProgramareDto> pacients) {
        return StreamSupport.stream(pacients.spliterator(), false)
                .map(x -> x.toEntityModel().add(linkTo(THIS_CLASS.getPacientById(x.getId_user_pacient())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }


    private CollectionModel<EntityModel<PacientDto>> pacientsToCollectionModel(List<Pacient> pacients) {
        return StreamSupport.stream(pacients.spliterator(), false)
                .map(x -> x.toEntityModelDto().add(linkTo(THIS_CLASS.getPacientById(x.getIdUser())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }


}
