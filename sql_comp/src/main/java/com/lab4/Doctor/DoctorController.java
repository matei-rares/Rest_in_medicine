package com.lab4.Doctor;

import com.lab4.Config.HeaderFilter;
import com.lab4.Pacient.Pacient;
import com.lab4.Pacient.PacientController;
import com.lab4.Pacient.PacientDto;
import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;
import com.lab4.Programari.ProgramareUpdateDto;
import com.lab4.enums.Specializare;
import com.lab4.enums.Status;
import com.lab4.model.MessageDto;
import com.lab4.service.ConversionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/medical_office/doctors")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "Authorization")
public class DoctorController {

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private IDoctor doctorService;
    private final DoctorController THIS_CLASS = methodOn(DoctorController.class);

    @Operation(summary = "Get all the doctors from database", responses = {@ApiResponse(
            responseCode = "200",
            description = "Doctors found"
    ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see doctors"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Request parameters are invalid"
            )

    })
    @GetMapping("/")
    public ResponseEntity<?> getAllDoctors(@RequestParam(name = "page", required = false) Optional<Integer> page,
                                             @RequestParam(value="items_per_page", required = false) Optional<Integer> items_per_page,
                                               @RequestParam(value="name", required = false) Optional<String> name,
                                               @RequestParam(value="specializare", required = false) Optional<String> specialization
                                               ) {
        if(HeaderFilter.getCurrentRole().equals("DOCTOR")){
            return new ResponseEntity<>("You are not allowed to access this resource", HttpStatus.FORBIDDEN);
        }

        if(page.isPresent())
        {
            if (page.get() < 0)
            {
                return new ResponseEntity<>("Paginile incep de la 0", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if(items_per_page.isPresent()){
                if(items_per_page.get() <= 0){
                    return new ResponseEntity<>("Nr de elemente pe pagina trebuie sa fie mai mare ca 0", HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
            else{
                items_per_page=Optional.of(2);
            }

            CollectionModel<EntityModel<DoctorDto>> result =doctorService.getAllDoctorsByPageParams(page, items_per_page);

            if( page.get()==0 && result.getContent().isEmpty()){
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
           result.getContent().stream().map(x->x.add(linkTo(THIS_CLASS.getDoctorById(x.getContent().getId_doctor())).withSelfRel().withType("GET"))).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        List<DoctorDto> list= new ArrayList<>();
        if(specialization.isPresent()){
            list =doctorService.findAllBySpecializare(specialization.get());
        }
        else if(name.isPresent()){
             list =doctorService.findAllByName(name.get());
          }
        else{
             list = doctorService.findAll();
        }

        CollectionModel<EntityModel<DoctorDto>> res=doctorsToCollectionModelDto(list)
                .add(linkTo(THIS_CLASS.getAllDoctors(page, items_per_page, name, specialization)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getAllDoctors(Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty())).withRel("parent").withType("GET"));
        if(list.isEmpty()){
            res.add(linkTo(THIS_CLASS.createDoctor(null)).withRel("create_doctor").withType("POST"));
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res,HttpStatus.OK);
    }



    @Operation(summary = "Get details of doctor", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pacient found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class))}
            ),
            @ApiResponse(
            responseCode = "401",
            description = "Need a correct authorization header"
    ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see doctor"
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(id.toString())) ){
            return new ResponseEntity<>("You are not allowed to access this resource", HttpStatus.FORBIDDEN);
        }
        Doctor doctor = doctorService.findByUserId(id);

        EntityModel<DoctorDto> result = doctor.toEntityModelDto()
                .add(linkTo(THIS_CLASS.getDoctorById(id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getAllDoctorAppointments( doctor.getIdUser())).withRel("get_appointments").withType("GET"))
                .add(linkTo(THIS_CLASS.createDoctorAppointment(id, null)).withRel("createAppointment").withType("POST"))
                .add(linkTo(THIS_CLASS.getAllDoctors(null,null,null,null)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Create a doctor", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A field is null or empty"
            ),  @ApiResponse(
            responseCode = "401",
            description = "Need a correct authorization header"
    ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to create doctors"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Request parameters are invalid"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Current doctor is in conflict with database"
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "Should be application/json"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid doctor field syntax"
            )
    })
    @PutMapping("/")
    public ResponseEntity<?> createDoctor(@Valid @RequestBody DoctorDto doctor) {
        if( !HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }
        Doctor doc = doctorService.save(conversionService.toEntity(doctor));
        EntityModel<DoctorDto> result = doc.toEntityModelDto()
                .add(linkTo(THIS_CLASS.getDoctorById(doc.getIdUser())).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getAllDoctorAppointments( doc.getIdUser())).withRel("get_appointments").withType("GET"))
                .add(linkTo(THIS_CLASS.createDoctorAppointment(doc.getIdUser(), null)).withRel("createAppointment").withType("POST"))
                .add(linkTo(THIS_CLASS.getAllDoctors(null,null,null,null)).withRel("parent").withType("GET"));
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }

    @Operation(summary = "Delete a doctor", responses = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DoctorDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to delete doctors"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }
        doctorService.deleteByUserId(id);
        return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
    }

    ////////////////////////////////////////////////////////////APPOINTMENTS///////////////////////////////////////////////

    @Operation(summary = "Get all appointments of a doctor", responses = {
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
                    description = "Not allowed to see appointments at this doctor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pacient not found"
            )
    })
      @GetMapping("/{id}/appointments")
    public ResponseEntity<?> getAllDoctorAppointments(@PathVariable Long id){
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }

          List<ProgramareDto> a=doctorService.findAllAppointmentsByUserId(id);
        if (a.isEmpty()) {
            CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                    .add(linkTo(THIS_CLASS.getAllDoctorAppointments(id)).withSelfRel().withType("GET"))
                    .add(linkTo(THIS_CLASS.getDoctorById(id)).withRel("parent").withType("GET"))
                    .add(linkTo(THIS_CLASS.createDoctorAppointment(id,null)).withRel("createPacient").withType("POST"));
            return new ResponseEntity<>(ar1, HttpStatus.OK);
        }
        CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                .add(linkTo(THIS_CLASS.getAllDoctorAppointments( id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getDoctorById(id)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(ar1, HttpStatus.OK);
    }


    @Operation(summary = "Add an appointment to a doctor", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment added",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProgramareDto.class))}
            ),  @ApiResponse(
            responseCode = "401",
            description = "Need a correct authorization header"
    ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to add appointments"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @PostMapping("/{id}/appointments")
        public ResponseEntity<?> createDoctorAppointment(@PathVariable Long id, @RequestBody ProgramareDto prog){
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are allowed to make this operation", HttpStatus.FORBIDDEN);
        }

        Programare prog1=conversionService.toEntity(prog);
        Programare res=doctorService.createAppointmentByUserId(id, prog1);
        ProgramareDto res1=conversionService.toDto(res);

        EntityModel<ProgramareDto> ent = EntityModel.of(res1)
                .add(linkTo(THIS_CLASS.createDoctorAppointment(id, null)).withSelfRel().withType("POST"))
                .add(linkTo(THIS_CLASS.getAllDoctorAppointments( id)).withRel("getAllAppointments").withType("GET"))
                .add(linkTo(THIS_CLASS.getDoctorById(id)).withRel("parent").withType("GET"))
                .add(Link.of("https://localhost:8081/consultations").withRel("createConsultation").withType("POST"));
        return new ResponseEntity<>(ent, HttpStatus.CREATED);
    }


    @Operation(summary = "Delete an appointment", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to this endpoint"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @DeleteMapping ("/{user_id}/pacient/{user_pacient_id}/appointments")
    public ResponseEntity<?> deleteDoctorAppointment(@PathVariable Long user_id, @PathVariable Long user_pacient_id, @RequestParam(name = "date",required = true) String date){
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(user_id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }
        //todo vezi daca are nevoie de 2 ore din browser
        System.out.println(date);
        String resultString=LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println(resultString);

        doctorService.deleteApp(user_id, user_id, user_pacient_id,resultString);

        return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
    }

/////////////////////////////////////////////PACIENTS///////////////////////////////////////////////
@Operation(summary = "Get all pacients of a doctor", responses = {
        @ApiResponse(
                responseCode = "200",
                description = "Pacients found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PacientDto.class))}
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Doctor not found"
        )
})
    @GetMapping("/{id}/pacients")
    public ResponseEntity<?> getAllDoctorPacients(@PathVariable Long id){
    if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
        return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
    }
        List<PacientDto> a=doctorService.findOwnPacientsByUserId(id);

        if (a.isEmpty()) {
            CollectionModel<EntityModel<PacientDto>> ar1 = pacientsToCollectionModel(a)
                    .add(linkTo(THIS_CLASS.getAllDoctorPacients(id)).withSelfRel().withType("GET"))
                    .add(linkTo(THIS_CLASS.getDoctorById(id)).withRel("parent").withType("GET"))
                    .add(linkTo(THIS_CLASS.createDoctorAppointment(id,null)).withRel("createAppointment").withType("POST"));
            return new ResponseEntity<>(ar1, HttpStatus.OK);
        }
    CollectionModel<EntityModel<PacientDto>> ar1 = pacientsToCollectionModel(a)
            .add(linkTo(THIS_CLASS.getAllDoctorPacients(id)).withSelfRel().withType("GET"))
            .add(linkTo(THIS_CLASS.getDoctorById(id)).withRel("parent").withType("GET"));
        return new ResponseEntity<>(ar1, HttpStatus.OK);
    }


    @Operation(summary = "Get all appointmentf of a doctor with a pacient", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pacients found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PacientDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments at this doctor"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or pacient not found"
            )
    })
    @GetMapping("/{doc_id}/pacient/{pac_id}/appointments")
    public ResponseEntity<?> getAllAppointmentsWithPacient(@PathVariable Long doc_id, @PathVariable Long pac_id){
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(doc_id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }
        List<ProgramareDto> a=doctorService.findAllAppointmentsOfPacient(doc_id,pac_id);

        if (a.isEmpty()) {
            CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                    .add(linkTo(THIS_CLASS.getAllAppointmentsWithPacient(doc_id,pac_id)).withSelfRel().withType("GET"))
                    .add(linkTo(THIS_CLASS.getDoctorById(doc_id)).withRel("parent").withType("GET"))
                    .add(linkTo(THIS_CLASS.createDoctorAppointment(doc_id,null)).withRel("createPacient").withType("POST"));
            return new ResponseEntity<>(ar1, HttpStatus.OK);
        }
        CollectionModel<EntityModel<ProgramareDto>> ar1 = programariToCollectionModel(a)
                .add(linkTo(THIS_CLASS.getAllAppointmentsWithPacient(doc_id,pac_id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getDoctorById(doc_id)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(ar1, HttpStatus.OK);
    }



    @Operation(summary = "Check if an appointment exists", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment exists"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments at this doctor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor/pacient/appointment not found"
            )
    })
    @GetMapping("/{doc_id}/pacient/{pac_id}/appointments/check")
    public ResponseEntity<?> checkAppointmentExistence(@PathVariable Long doc_id, @PathVariable Long pac_id ,  @RequestParam(name = "date", required = true) Optional<String> dateParam){
        if( (HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(doc_id.toString()))  || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")){
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }

        if(dateParam.isEmpty()){
            return new ResponseEntity<>("Date param is mandatory", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        ProgramareDto prog= doctorService.findAppByDocIdPacIdAndDate(doc_id,pac_id,dateParam.get());
        if(prog==null){
            return new ResponseEntity<>("Appointment doesn't exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Appointment exists", HttpStatus.OK);
    }


    @Operation(summary = "Update status for an appointment", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment updated"
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
                    description = "Not allowed to see appointments at this doctor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor/pacient/appointment not found"
            )
    })
    @PatchMapping("/{doc_id}/pacient/{pac_id}/appointments")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long doc_id, @PathVariable Long pac_id ,  @RequestParam(name = "date", required = true) Optional<String> dateParam, @RequestBody ProgramareUpdateDto statusParam){
        if ((HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(doc_id.toString())) || HeaderFilter.getCurrentRole().equals("PACIENT") || HeaderFilter.getCurrentRole().equals("ADMIN")) {
            return new ResponseEntity<>("You are not allowed to do this operation", HttpStatus.FORBIDDEN);
        }

        if (dateParam.isEmpty()) {
            return new ResponseEntity<>("Date param is mandatory", HttpStatus.BAD_REQUEST);
        }
        if (statusParam == null || statusParam.getStatus() == null) {
            return new ResponseEntity<>("Status param is mandatory", HttpStatus.BAD_REQUEST);
        }
        System.out.println(dateParam.get());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS '00:00'");
        LocalDateTime dateTime = LocalDateTime.parse(dateParam.get(), formatter);
        LocalDateTime resultDateTime = dateTime.plusHours(2);

        String resultString = resultDateTime.format(formatter);
        System.out.println(resultString);


        Programare prog = doctorService.modifyApp(doc_id, pac_id, resultString,statusParam.getStatus().toString());

        EntityModel<ProgramareDto> ent= conversionService.toDto(prog).toEntityModel()
                .add(linkTo(THIS_CLASS.updateAppointmentStatus(doc_id,pac_id,dateParam,statusParam)).withSelfRel().withType("PATCH"))
                .add(linkTo(THIS_CLASS.getAllAppointmentsWithPacient(doc_id,pac_id)).withRel("parent").withType("GET"));

        return new ResponseEntity<>(ent, HttpStatus.OK);
    }





        private CollectionModel<EntityModel<DoctorDto>> doctorsToCollectionModelDto(List<DoctorDto> doctors) {
        return StreamSupport.stream(doctors.spliterator(), false)
                .map(x -> x.toEntityModel().add(linkTo(THIS_CLASS.getDoctorById(x.getId_doctor())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }



    private CollectionModel<EntityModel<ProgramareDto>> programariToCollectionModel(List<ProgramareDto> pacients) {
        return StreamSupport.stream(pacients.spliterator(), false)
                .map(x -> x.toEntityModel().add(linkTo(THIS_CLASS.getDoctorById(x.getId_user_pacient())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
    private CollectionModel<EntityModel<PacientDto>> pacientsToCollectionModel(List<PacientDto> pacients) {
        return StreamSupport.stream(pacients.spliterator(), false)
                .map(x -> x.toEntityModel().add(linkTo(methodOn(PacientController.class).getPacientById(x.getIdUser())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }

}
