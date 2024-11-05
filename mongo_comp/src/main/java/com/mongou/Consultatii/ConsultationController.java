package com.mongou.Consultatii;

import com.mongou.Investigatii.Investigation;
import com.mongou.Investigatii.InvestigationDto;
import com.mongou.config.HeaderFilter;
import com.mongou.execeptions.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController()
@RequestMapping("/api")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "Authorization")
public class ConsultationController {
    @Autowired
    private IConsultation consultationService;

    @Autowired
    private ConsultationRepository consultationRepository;
    private final ConsultationController THIS_CLASS = methodOn(ConsultationController.class);

    @Operation(summary = "Get a consultation by id", responses = {@ApiResponse(
            responseCode = "200",
            description = "Consultation found"
    ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Need a correct authorization header"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments at this doctor"
            )})
    @GetMapping("/consultation/{id}")
    public ResponseEntity<?> getConsultation(@PathVariable String id) {
        if(HeaderFilter.getCurrentRole().equals("ADMIN") ){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }

        Consultation cons = consultationRepository.findById(new ObjectId(id));

        EntityModel<Consultation> consModel = EntityModel.of(cons).add(linkTo(THIS_CLASS.getConsultation(id)).withSelfRel().withType("GET"))
                .add(linkTo(THIS_CLASS.getConsultationsBy(null, null, null)).withRel("parent").withType("GET"));
        if (cons == null) {
            consModel.add(linkTo(THIS_CLASS.createConsultation(null)).withRel("createConsultation").withType("POST"));
            return new ResponseEntity<>(consModel, HttpStatus.OK);
        }
        if((HeaderFilter.getCurrentRole().equals("DOCTOR") && !HeaderFilter.getCurrentSub().equals(cons.getId_doctor().toString()))
                || (HeaderFilter.getCurrentRole().equals("PACIENT") && !HeaderFilter.getCurrentSub().equals(cons.getId_pacient().toString()))){
            return new ResponseEntity<>("You don't access to this resource",HttpStatus.FORBIDDEN);
        }


        return new ResponseEntity<>(consModel, HttpStatus.OK);
    }


    @Operation(summary = "Get all consultations by params", responses = {@ApiResponse(
            responseCode = "200",
            description = "Consultations found"
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
                    responseCode = "422",
                    description = "Invalid request params"
            )
    })
    @GetMapping("/consultations")
    public ResponseEntity<?> getConsultationsBy(@RequestParam(name = "pacient", required = false) Optional<Long> pacientID,
                                                @RequestParam(name = "doctor", required = false) Optional<Long> doctorID,
                                                @RequestParam(name = "date", required = false) Optional<String> date
    ) {
        if(HeaderFilter.getCurrentRole().equals("ADMIN")
                || (HeaderFilter.getCurrentRole().equals("DOCTOR") && doctorID.isPresent() && !HeaderFilter.getCurrentSub().equals(doctorID.get().toString()))
                || (HeaderFilter.getCurrentRole().equals("PACIENT") && pacientID.isPresent() && !HeaderFilter.getCurrentSub().equals(pacientID.get().toString()))){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }

        List<Consultation> res = new ArrayList<>();
        if (pacientID.isPresent() && doctorID.isPresent() && date.isPresent()) {
            res = consultationService.findConsByDocPacAndDate(doctorID.get(), pacientID.get(), date.get());
        } else if (pacientID.isPresent() && doctorID.isPresent()) {
            res = consultationRepository.findAllByPacientAndDoctor(pacientID.get(), doctorID.get());
        } else if (pacientID.isPresent()) {
            res = consultationRepository.findAllByPacientId(pacientID.get());
        } else if (doctorID.isPresent()) {
            res = consultationRepository.findAllByDoctorId(doctorID.get());
        } else {
            res = consultationService.getAllConsultations();
        }
        CollectionModel<EntityModel<ConsultationDto>> cons = consulationsToCollection(res)
                .add(linkTo(THIS_CLASS.getConsultationsBy(pacientID, doctorID, date)).withSelfRel().withType("GET"));

        if (res.isEmpty()) {
            cons.add(linkTo(THIS_CLASS.createConsultation(null)).withRel("createConsultation").withType("POST"));
            return new ResponseEntity<>(cons, HttpStatus.OK);
        }
        System.out.println(res.get(0).getInvestigations());
        return new ResponseEntity<>(cons, HttpStatus.OK);

    }

    @Operation(summary = "Create a consultation", responses = {@ApiResponse(
            responseCode = "201",
            description = "Consultation created"
    ),@ApiResponse(
            responseCode = "401",
            description = "Invalid body fields"
    ),   @ApiResponse(
            responseCode = "401",
            description = "Need a correct authorization header"
    ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments at this doctor"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Consultation already exists"
            )
    })
    @PostMapping("/consultations")
    public ResponseEntity<?> createConsultation(@RequestBody ConsultationDto consultation) {
        if(HeaderFilter.getCurrentRole().equals("PACIENT")  || !HeaderFilter.getCurrentSub().equals(consultation.getId_doctor().toString())){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }
        System.out.println(consultation.getDate().toString());
        ConsultationValidator.validate(consultation);
        int responseCode=0;
        try {

            System.out.println(consultation.getDate().toString());
            String outputDateString = LocalDateTime.ofInstant(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(consultation.getDate().toString()).toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            String apiUrl = "http://localhost:8080/api/medical_office/doctors/" + consultation.getId_doctor() + "/pacient/" + consultation.getId_pacient() + "/appointments/check?date=" + outputDateString;
            System.out.println(apiUrl);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + HeaderFilter.getCurrentToken());
             responseCode = connection.getResponseCode();



        }
        catch (Exception e){

            throw new CustomException("Can't process",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(responseCode != HttpURLConnection.HTTP_OK){
            System.out.println("Error: " + responseCode);
            throw new CustomException("Appointment doesn't exist in order to create a consultation",HttpStatus.NOT_FOUND);
        }


        String formatted = new SimpleDateFormat("yyyy-MM-dd").format(consultation.getDate()).substring(0, 10);
        List<Consultation> consults = consultationService.findConsByDocPacAndDate(consultation.getId_doctor(), consultation.getId_pacient(), formatted);


        if (!consults.isEmpty()) {
            return new ResponseEntity<>("Exista deja o consultatie la aceasta data", HttpStatus.CONFLICT);
        }

        Consultation res = consultationService.saveConsultation(new Consultation(consultation));
        EntityModel<ConsultationDto> cons= res.toEntityModelDto()
                .add(linkTo(THIS_CLASS.createConsultation(null)).withSelfRel().withType("POST"))
                .add(linkTo(THIS_CLASS.getConsultationsBy(null, null, null)).withRel("parent").withType("GET"));
        return new ResponseEntity<>(cons, HttpStatus.CREATED);
    }



    @Operation(summary = "Create an investigation", responses = {@ApiResponse(
            responseCode = "201",
            description = "Investigation created"
    ),@ApiResponse(
            responseCode = "401",
            description = "Invalid body fields"
    ),   @ApiResponse(
            responseCode = "401",
            description = "Need a correct authorization header"
    ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to see appointments at this doctor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultation doesn't exist"
            )
    })
    @PostMapping("/consultations/{id}")
    public ResponseEntity<?> addInvestigatie(
            @PathVariable String id,
            @RequestBody InvestigationDto investigation) {

        if(HeaderFilter.getCurrentRole().equals("PACIENT") ){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }
        ConsultationValidator.validateInvestigation(investigation);

        System.out.println(id);
        Optional<Consultation> cs = consultationRepository.findById(id);
        System.out.println(cs);
        if (cs.isPresent()) {

            if(!HeaderFilter.getCurrentSub().equals(cs.get().getId_doctor().toString())){
                return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
            }

            Consultation consultation = cs.get();
            consultation.getInvestigations().add(new Investigation(investigation));
            Consultation consu=consultationRepository.save(consultation);

            EntityModel<ConsultationDto> cons= consu.toEntityModelDto()
                    .add(linkTo(THIS_CLASS.addInvestigatie(id,null)).withSelfRel().withType("POST"))
                    .add(linkTo(THIS_CLASS.getConsultationsBy(null, null, null)).withRel("parent").withType("GET"));
            return new ResponseEntity<>(cons, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Nu exista consultatia", HttpStatus.NOT_FOUND);
        }

    }
    @Operation(summary = "Update an investigation", responses = {@ApiResponse(
            responseCode = "201",
            description = "Investigation updated"
    ),@ApiResponse(
            responseCode = "401",
            description = "Invalid body fields"
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
                    description = "Consultation doesn't exist"
            )
    })
    @PatchMapping("/consultations/{id}/update")
    public ResponseEntity<?> updateInvestigatie(
            @PathVariable String id,
            @RequestBody InvestigationDto investigation) {
        if(HeaderFilter.getCurrentRole().equals("PACIENT") ){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }
        ConsultationValidator.validateInvestigation(investigation);
        ConsultationValidator.validateObjectid(investigation.getId());

        Optional<Consultation> cs = consultationRepository.findById(id);
        if (cs.isPresent()) {

            if(!HeaderFilter.getCurrentSub().equals(cs.get().getId_doctor().toString())){
                return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
            }



            Consultation consultation = cs.get();
            List<Investigation> investigations = consultation.getInvestigations();
            for (int i = 0; i < investigations.size(); i++) {


                if (investigations.get(i).getId().equals(investigation.getId())) {
                    System.out.println("gasit");

                    consultation.getInvestigations().get(i).setRezultat(investigation.getRezultat());
                    consultation.getInvestigations().get(i).setDenumire(investigation.getDenumire());
                    consultation.getInvestigations().get(i).setDurata_de_procesare(investigation.getDurata_de_procesare());
                    Consultation newcons=consultationRepository.save(consultation);

                    EntityModel<ConsultationDto> cons= newcons.toEntityModelDto()
                            .add(linkTo(THIS_CLASS.updateInvestigatie(id,null)).withSelfRel().withType("PATCH"))
                            .add(linkTo(THIS_CLASS.getConsultationsBy(null, null, null)).withRel("parent").withType("GET"));
                    return new ResponseEntity<>(cons, HttpStatus.OK);
                }
            }
            //consultationRepository.save(consultation);
            return new ResponseEntity<>("Investigation not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Consultation not found", HttpStatus.NOT_FOUND);
        }


    }
    @Operation(summary = "Update a consultation", responses = {@ApiResponse(
            responseCode = "201",
            description = "Consultation updated"
    ),@ApiResponse(
            responseCode = "401",
            description = "Invalid body fields"
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
                    description = "Consultation doesn't exist"
            )
    })
    @PatchMapping("/consultations/{id}")
    public ResponseEntity<?> updateConsultatie(@PathVariable  String id, @RequestBody UpdateConsultatieDto newCons) {
        if(HeaderFilter.getCurrentRole().equals("PACIENT") ){
            return new ResponseEntity<>("You don't access to this operation",HttpStatus.FORBIDDEN);
        }
        ConsultationValidator.validateDiagnostic(newCons.getDiagnostic());
        Optional<Consultation> cs = consultationRepository.findById(id);
        if (cs.isPresent()) {

            if (!HeaderFilter.getCurrentSub().equals(cs.get().getId_doctor().toString())) {
                return new ResponseEntity<>("You don't access to this operation", HttpStatus.FORBIDDEN);
            }
            cs.get().setDiagnostic(newCons.getDiagnostic());
            Consultation newConss=consultationRepository.save(cs.get());
            EntityModel<ConsultationDto> cons = newConss.toEntityModelDto()
                    .add(linkTo(THIS_CLASS.updateConsultatie(id, null)).withSelfRel().withType("PATCH"))
                    .add(linkTo(THIS_CLASS.getConsultationsBy(null, null, null)).withRel("parent").withType("GET"));
            return new ResponseEntity<>(cons, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Consultation not found", HttpStatus.NOT_FOUND);
        }

    }

    private CollectionModel<EntityModel<ConsultationDto>> consulationsToCollection(List<Consultation> pacients) {
        return StreamSupport.stream(pacients.spliterator(), false)
                .map(x -> x.toEntityModelDto().add(linkTo(THIS_CLASS.getConsultation(x.getId())).withSelfRel().withType("GET")))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }

}
