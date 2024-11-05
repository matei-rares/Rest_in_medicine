package com.lab4.Doctor;

import com.lab4.Pacient.Pacient;
import com.lab4.Pacient.PacientDto;
import com.lab4.Pacient.PacientRepository;
import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;
import com.lab4.Programari.ProgramariRepository;
import com.lab4.Programari.ProgramariValidator;
import com.lab4.enums.Specializare;
import com.lab4.enums.Status;
import com.lab4.exceptions.DoctorException;
import com.lab4.exceptions.DoctorSqlException;
import com.lab4.exceptions.PacientSqlException;
import com.lab4.exceptions.ProgramareSqlException;
import com.lab4.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class DoctorService implements IDoctor{

    @Autowired
    private final DoctorRepository doctorRepository;

    @Autowired
    private final ProgramariRepository programariRepository;

    @Autowired
    private final ConversionService conversionService;

    @Autowired
    private final PacientRepository pacientRepository;
    @Autowired
    private final DoctorPageRepository doctorPageRepository;

    public DoctorService(DoctorRepository doctorRepository, ProgramariRepository programariRepository, ConversionService conversionService, PacientRepository pacientRepository, DoctorPageRepository doctorPageRepository){
        this.doctorRepository = doctorRepository;
        this.programariRepository = programariRepository;
        this.conversionService = conversionService;
        this.pacientRepository = pacientRepository;
        this.doctorPageRepository = doctorPageRepository;
    }



    public Doctor save(Doctor doctor){
        DoctorValidator.validate(doctor);

        Optional<Doctor> doc= doctorRepository.findByIdUser(doctor.getIdUser());
        if (doc.isPresent()){
            doc.get().setNume(doctor.getNume());
            doc.get().setPrenume(doctor.getPrenume());
            doc.get().setEmail(doctor.getEmail());
            doc.get().setTelefon(doctor.getTelefon());
            doc.get().setSpecializare(doctor.getSpecializare());

            throw new DoctorException("",HttpStatus.NO_CONTENT  );
        }

        doctorRepository.findByEmail(doctor.getEmail()).ifPresent(pacient1 -> {
            throw new DoctorSqlException("Un alt doctor are emailul " + doctor.getEmail(), HttpStatus.CONFLICT );
        });
        doctorRepository.findByIdUser(doctor.getIdUser()).ifPresent(pacient1 -> {
            throw new DoctorSqlException("Doctorul cu user id-ul " + doctor.getIdUser() + " exista deja in baza de date", HttpStatus.CONFLICT);
        });

        Doctor result=doctorRepository.save(doctor);

        return result;
    }

    public List<DoctorDto> findAll(){

        List<DoctorDto> res=doctorRepository.findAll().stream().map(x->conversionService.toDto(x)).toList();
        return res;
    }

    public List<DoctorDto> findAllBySpecializare(String spec){

        Specializare specEnum;
        if(spec == null || spec.isEmpty()){
            throw new DoctorSqlException("Specializarea daca e presenta nu trebuie sa fie goala",HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try{
            specEnum=Specializare.valueOf(spec.toUpperCase());
        }
        catch (Exception e){
            throw new DoctorSqlException("Specializarea " + spec + " nu exista",HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<DoctorDto> doctors=doctorRepository.getDoctorsBySpecializare(specEnum).stream().map(x->conversionService.toDto(x)).toList();
        return doctors;

    }

    public List<DoctorDto> findAllByName(String nume){
        DoctorValidator.validateName(nume,"ok");
        return doctorRepository.findAllByNumeContaining(nume).stream().map(x->conversionService.toDto(x)).toList();
    }

    @Override
    public Doctor findById(Long id) {
        Optional<Doctor> result = doctorRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        else{
            throw new DoctorSqlException("Doctorul cu id-ul " + id + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }
    }

    public Doctor findByUserId(Long id){
        Optional<Doctor> result = doctorRepository.findByIdUser(id);
        if(result.isPresent()){
            return result.get();
        }
        else{
            throw new DoctorSqlException("Doctorul cu user id-ul " + id + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public Doctor deleteById(Long id){
        Doctor result = findById(id);
        doctorRepository.deleteById(id);
        return result;
    }
    public Doctor deleteByUserId(Long id){
        Doctor result = findByUserId(id);
        doctorRepository.delete(result);
        return result;
    }
    ////////////////////////////////////////////////////////////APPOINTMENTS///////////////////////////////////////////////


    public List<ProgramareDto> findAllAppointmentsByUserId(Long id){
        Doctor doc=findByUserId(id);
        List<ProgramareDto> a=programariRepository.findAllByDoctorIdDoctor(doc.getIdDoctor()).stream().map(x->conversionService.toDto(x)).toList();
        return a;
    }

    public Programare createAppointmentByUserId(Long id, Programare prog){

        Doctor doc= findByUserId(id); // verific id doctor

        if (doc.getIdDoctor().equals(prog.getId_doctor()) == false){ // verific conflict
            throw new DoctorSqlException("Id-ul doctorului nu corespunde cu cel din programare",HttpStatus.CONFLICT);//CONFLICT
        }

        Pacient pac=findPacientByCnp(prog.getId_pacient()); // verific id pacient
        ProgramariValidator.validateDate(prog.getDate()); // verific data
        ProgramariValidator.validateNullStatus(prog.getStatus()); // verific status

        //iau toate programarile de la pacient si de la doctor pt a verifica disponibilitatea
        List<Programare> allApp=programariRepository.findAllByPacientCnpOrDoctorIdDoctor(pac.getCnp(),doc.getIdDoctor());

        LocalDateTime startApp = prog.getDate().toInstant().atZone(ZoneId.of("Europe/Bucharest")).toLocalDateTime();
        LocalDateTime beforeStart15 = startApp.minusMinutes(15);
        LocalDateTime afterStart15 = startApp.plusMinutes(15);

        //verificari datzi
        for (Programare pr:allApp){

            ///daca programare noua se potriveste intre celelalte
            LocalDateTime currProgramareDate = pr.getDate().toInstant().atZone(ZoneId.of("Europe/Bucharest")).toLocalDateTime();
            if (currProgramareDate.isAfter(beforeStart15) && currProgramareDate.isBefore(afterStart15)) {
                throw new ProgramareSqlException("Data programarii trebuie sa fie cu cel putin 15 diferenta de alta programare.",HttpStatus.CONFLICT);//Conflict
            }

            //daca programarea noua are alta zi sau alt doctor
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currProgramareDay = sdf.format(pr.getDate());
            String newProgramareDay = sdf.format(prog.getDate());
            if ( currProgramareDay.equals(newProgramareDay) && pr.getId_doctor().equals(prog.getId_doctor())){
                throw new ProgramareSqlException("Pacientul nu poate avea programari in aceeasi zi cu acelasi doctor",HttpStatus.CONFLICT);//Conflict
            }
        }

        Programare res= programariRepository.save(prog);
        return res;
    }


    public Pacient findPacientByCnp(String cnp) { //resursa specifica unui pacient

        Optional<Pacient> result = pacientRepository.findById(cnp);
        if(result.isPresent())
            return result.get();
        else
            throw new PacientSqlException("Pacientul cu cnp-ul " + cnp + " nu exista in baza de date",HttpStatus.NOT_FOUND);
    }

    public CollectionModel<EntityModel<DoctorDto>> getAllDoctorsByPageParams(Optional<Integer> page, Optional<Integer> items_per_page) {
        int finalItemsPerPage = items_per_page.orElse(9);

        PageRequest pageRequest = PageRequest.of(page.get(), finalItemsPerPage);
        Page<Doctor> doctorsPage = doctorPageRepository.findAll(pageRequest);

        List<EntityModel<DoctorDto>> doctors = doctorsPage.getContent().stream()
                .map(entity -> EntityModel.of(conversionService.toDto(entity)/*,adaug aici linkuri pentru entitate*/))
                .collect(Collectors.toList());

        var pagedModel = PagedModel.of(doctors,
                linkTo(methodOn(DoctorController.class)
                        .getAllDoctors(page, items_per_page, Optional.empty(), Optional.empty())).withSelfRel());

        if(doctors.isEmpty() && page.get() >= doctorsPage.getTotalPages()){ //daca pagina curenta nu are doctori, link catre ultima care are elemente
            pagedModel.add(linkTo(methodOn(DoctorController.class)
                    .getAllDoctors(Optional.of(doctorsPage.getTotalPages() - 1), items_per_page, Optional.empty(), Optional.empty())).withRel("lastPageWithDoctors"));
            return pagedModel;
        }

        if (doctorsPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(DoctorController.class)
                    .getAllDoctors(Optional.of(page.get() - 1), items_per_page, Optional.empty(), Optional.empty())).withRel("prev"));
        }
        if (doctorsPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(DoctorController.class)
                    .getAllDoctors(Optional.of(page.get() + 1), items_per_page, Optional.empty(), Optional.empty())).withRel("next"));
        }

        return pagedModel;
    }
    public List<PacientDto> findOwnPacientsByUserId(Long user_id){
        Doctor doc=findByUserId(user_id);
        List<Programare> programari=programariRepository.findAllByDoctorIdDoctor(doc.getIdDoctor());
        List<String> cnps=programari.stream().map(Programare::getId_pacient).toList();
        List<String> distinctList = new ArrayList<>();
        for (String element : cnps) {
            if (!distinctList.contains(element)) {
                distinctList.add(element);
            }
        }
        List<PacientDto> pacienti=distinctList.stream().map(x-> findPacientByCnp(x).toDto()).filter(PacientDto::getIsActive).toList();
        return pacienti;
    }


    public List<ProgramareDto> findAllAppointmentsOfPacient(Long doc_id, Long pac_id){
        Doctor doc=findByUserId(doc_id);
        Optional<Pacient> pac=pacientRepository.findByIdUser(pac_id);
        if(pac.isEmpty()){
            throw new PacientSqlException("Pacientul cu user id-ul " + pac_id + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }
        List<Programare> programari=programariRepository.findAllByPacientCnpAndDoctorIdDoctor(pac.get().getCnp(),doc.getIdDoctor());
        return programari.stream().map(x->conversionService.toDto(x)).toList();
    }

    public ProgramareDto findAppByDocIdPacIdAndDate(Long doc_id, Long pac_id, String date){

        List<ProgramareDto> a=findAllAppointmentsOfPacient(doc_id,pac_id);
        String param=date.replace("T"," ").substring(0,16);
        ProgramareDto res=null;
        for(ProgramareDto p : a){
            System.out.println("app: "+p.getData().toString().substring(0,16));
            System.out.println("param: "+param);
            if(p.getData().toString().substring(0,16).equals(param)){
                System.out.println("Appointment exists");
                res=p;
                break;
            }
        }

        return res;
    }
    public void deleteApp(Long id,Long doc_id, Long pac_id, String date){

        if(id != doc_id){
            throw new ProgramareSqlException("Nu poti sterge programarea altcuiva",HttpStatus.BAD_REQUEST);
        }

        ProgramareDto prog=findAppByDocIdPacIdAndDate(doc_id,pac_id,date);
        if(prog==null){
            throw new ProgramareSqlException("Programarea nu exista",HttpStatus.NOT_FOUND);
        }
        Programare prog1=conversionService.toEntity(prog);
        programariRepository.delete(prog1);
    }

    public Programare modifyApp(Long doc_id, Long pac_id, String date, String statusParam){

        Status specEnum;
        if(statusParam == null || statusParam.isEmpty()){
            throw new DoctorSqlException("Status nu trebuie sa fie gol",HttpStatus.BAD_REQUEST);
        }
        try{
            specEnum=Status.valueOf(statusParam.toUpperCase());
        }
        catch (Exception e){
            throw new DoctorSqlException("Staatusul " + statusParam + " nu exista",HttpStatus.NOT_FOUND);
        }


        ProgramareDto prog=findAppByDocIdPacIdAndDate(doc_id,pac_id,date);
        if(prog==null){
            throw new ProgramareSqlException("Programarea nu exista",HttpStatus.NOT_FOUND);
        }
        prog.setStatus(specEnum);
        Programare prog1=conversionService.toEntity(prog);
        return programariRepository.save(prog1);
    }



}
