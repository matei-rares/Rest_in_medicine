package com.lab4.Pacient;

import com.lab4.Doctor.Doctor;
import com.lab4.Doctor.DoctorRepository;
import com.lab4.Programari.Programare;
import com.lab4.Programari.ProgramareDto;
import com.lab4.Programari.ProgramariRepository;
import com.lab4.Programari.ProgramariValidator;
import com.lab4.exceptions.*;
import com.lab4.service.ConversionService;
import com.lab4.validators.ParamValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Service
public class PacientService implements IPacient{
    @Autowired
    private final PacientRepository pacientRepository;
    @Autowired
    private final ProgramariRepository programariRepository;

    @Autowired
    private final ConversionService conversionService;

    @Autowired
    private final DoctorRepository doctorRepository;


    public PacientService(PacientRepository repo, ProgramariRepository programariRepository, ConversionService conversionService, DoctorRepository doctorRepository){
        this.pacientRepository=repo;
        this.programariRepository = programariRepository;
        this.conversionService = conversionService;
        this.doctorRepository = doctorRepository;

    }
    public Pacient saveOrUpdate(Pacient pacient){
        PacientValidator.validate(pacient);
        Pacient result;

        Optional<Pacient> existingPacient=pacientRepository.findByIdUser(pacient.getIdUser());
        if(existingPacient.isPresent()){
            if(!existingPacient.get().getCnp().equals(pacient.getCnp())){
                throw new PacientSqlException("Nu poti schimba cnp-ul ", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            existingPacient.get().setNume(pacient.getNume());
            existingPacient.get().setPrenume(pacient.getPrenume());
            existingPacient.get().setEmail(pacient.getEmail());
            existingPacient.get().setTelefon(pacient.getTelefon());
            existingPacient.get().setDataNasterii(pacient.getDataNasterii());
            existingPacient.get().setIsActive(pacient.getIsActive());
            pacientRepository.save(existingPacient.get());
            throw new PacientException("",HttpStatus.NO_CONTENT  );
        }


        pacientRepository.findByCnp(pacient.getCnp()).ifPresent(pacient1 -> {
            throw new PacientSqlException("Pacientul cu cnp-ul " + pacient.getCnp() + " exista deja in baza de date", HttpStatus.CONFLICT);
        });
        pacientRepository.findByEmail(pacient.getEmail()).ifPresent(pacient1 -> {
            throw new PacientSqlException("Un alt pacient are emailul " + pacient.getEmail(),HttpStatus.CONFLICT );
        });

        pacient.setIsActive(true); // daca nu e introdus campul la inregistrare se pune automat pe true


        return pacientRepository.save(pacient);
    }
    public Pacient save(Pacient pacient){
        PacientValidator.validate(pacient);
        Pacient result;

        pacientRepository.findByCnp(pacient.getCnp()).ifPresent(pacient1 -> {
            throw new PacientSqlException("Pacientul cu cnp-ul " + pacient.getCnp() + " exista deja in baza de date", HttpStatus.CONFLICT);
        });

        pacientRepository.findByEmail(pacient.getEmail()).ifPresent(pacient1 -> {
            throw new PacientSqlException("Un alt pacient are emailul " + pacient.getEmail(),HttpStatus.CONFLICT );
        });
        pacientRepository.findByIdUser(pacient.getIdUser()).ifPresent(pacient1 -> {
            throw new PacientSqlException("Pacientul cu user id-ul " + pacient.getIdUser() + " exista deja in baza de date", HttpStatus.CONFLICT);
        });

        pacient.setIsActive(true); // daca nu e introdus campul la inregistrare se pune automat pe true


        result=pacientRepository.save(pacient);

        return result;
    }

    public List<Pacient> findAll(){
        return pacientRepository.findAll();
    }

    public Pacient findByCnp(String cnp) {


        Optional<Pacient> result = pacientRepository.findByCnp(cnp);
        if(result.isPresent())
            return result.get();
        else
            throw new PacientSqlException("Pacientul cu id-ul " + cnp + " nu exista in baza de date",HttpStatus.NOT_FOUND);
    }


    public Pacient findByUserId(Long userid) {
        Optional<Pacient> result = pacientRepository.findByIdUser(userid);
        if(result.isPresent())
            return result.get();
        else
            throw new PacientSqlException("Pacientul cu user id-ul " + userid + " nu exista in baza de date",HttpStatus.NOT_FOUND);
    }



    public Pacient deleteByUserId(Long id){ //resursa specifica unui pacient
        Pacient result = this.findByUserId(id);
        pacientRepository.deleteById(result.getCnp());
        return result;
    }



    public Long getUserIdBasedOnCnp(String Cnp){
        return findByCnp(Cnp).getIdUser();
    }

    ////////////////////////////////////////////////////////////APPOINTMENTS///////////////////////////////////////////////




    public List<ProgramareDto> findAllAppointmentsByCnp(String cnp){
        Pacient pac= this.findByCnp(cnp);
        List<ProgramareDto> a=programariRepository.findAllByPacientCnp(pac.getCnp()).stream().map(x->conversionService.toDto(x)).toList();
        return a;

    }

    public Programare createAppointmentByUserId(Long id, Programare prog){
        System.out.println(prog.getDate());

        Pacient pac= this.findByUserId(id);

        if (pac.getCnp().equals(prog.getId_pacient()) == false){ // verific conflict
            System.out.println(pac.getCnp() +"   "+ prog.getId_pacient());
            throw new PacientSqlException("Id-ul pacientului nu corespunde cu cel din programare",HttpStatus.CONFLICT);//CONFLICT
        }

        Doctor doc=this.findDoctorById(prog.getId_doctor()); // verific id doctor
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

    public Doctor findDoctorById(Long id) {
        Optional<Doctor> result = doctorRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        else{
            throw new PacientSqlException("Doctorul cu  id-ul " + id + " nu exista in baza de date",HttpStatus.NOT_FOUND);
        }
    }


    public List<ProgramareDto> getAllPacientAppointmentsWithParams(Long userID,Optional<String> dateParam,Optional<String> typeParam){
        Pacient pacient=this.findByUserId(userID);
        String cnp=pacient.getCnp();

        if(dateParam.isPresent()){//daca exista date
            String date=dateParam.get();
            if(typeParam.isPresent()){//exista si type
                String type=typeParam.get();
                ParamValidator.validateType(type);
                int dateInt=ParamValidator.validateDateWithType(date,type);

                List<ProgramareDto> a=findAllAppointmentsByCnp(cnp);
                List<ProgramareDto> found=new ArrayList<>();
                switch (type){
                    case "year":
                        for(ProgramareDto prog:a){
                            if((prog.getData().getYear()+1900)==dateInt){
                                found.add(prog);
                            }
                        }
                        break;
                    case "month":
                        for(ProgramareDto prog:a){
                            if((prog.getData().getMonth()+1)==dateInt){
                                found.add(prog);
                            }
                        }
                        break;
                    case "day":
                        Calendar calendar = Calendar.getInstance();
                        for(ProgramareDto prog:a){
                            calendar.setTime(prog.getData());
                            int day=calendar.get(Calendar.DAY_OF_MONTH);
                            if(day==dateInt){
                                found.add(prog);
                            }
                        }
                        break;
                }
                return found;
            }
            //nu exista type, date trebuie sa fie de forma yyyy-MM-dd
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                System.out.print(dateFormat.parse(date));
            } catch ( ParseException e) {
                throw new ParamsException("In lipsa lui type, date trebuie sa fie de forma yyyy-MM-dd", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            List<ProgramareDto> a=findAllAppointmentsByCnp(cnp);
            List<ProgramareDto> found=new ArrayList<>();

            for(ProgramareDto prog:a){
                String simpleFormate=dateFormat.format(prog.getData());
                if(simpleFormate.equals(date)){
                    found.add(prog);
                }
            }
            return found;
        }

        //daca nu exista date si exista type
        if(typeParam.isPresent()){
                throw new ParamsException("Este nevoie si de date in prezenta lui type", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //get fara request params
        List<ProgramareDto> a=findAllAppointmentsByCnp(cnp);
        return a;
    }

    public Pacient updatePacientById(Long id, UpdatePacientDto pacient){

        Pacient a =findByUserId(id);
        System.out.println(a.toDto().toString());
        if (pacient.getIsActive() == null) {
            throw new PacientException("IsActive field should not be null", HttpStatus.BAD_REQUEST);
        }
        if (!(pacient.getIsActive() instanceof Boolean)) {
            throw new PacientException("IsActive ar trebui sa fie o valoarea de true/false", HttpStatus.BAD_REQUEST);
        }

        a.setIsActive(pacient.getIsActive());
        return pacientRepository.save(a);
    }


    public Pacient replacePacientData(Long uid,PacientDto pac){
        Pacient a = findByUserId(uid);
        PacientValidator.validateEmail(pac.getEmail());
        PacientValidator.validateName(pac.getNume(),pac.getPrenume());
        PacientValidator.validatePhoneNumber(pac.getTelefon());
        PacientValidator.validateDateOfBirth(pac.getDataNasterii());

        a.setNume(pac.getNume());
        a.setPrenume(pac.getPrenume());
        a.setEmail(pac.getEmail());
        a.setTelefon(pac.getTelefon());
        a.setDataNasterii(pac.getDataNasterii());
        return pacientRepository.save(a);
    }

}
