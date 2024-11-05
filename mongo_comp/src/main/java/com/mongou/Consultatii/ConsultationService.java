package com.mongou.Consultatii;

import com.mongou.Investigatii.Investigation;
import com.mongou.Investigatii.InvestigationRepository;
import com.mongou.execeptions.ConsultationException;
import com.mongou.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultationService implements IConsultation {
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private InvestigationRepository investigationRepository;
    @Autowired
    private ConversionService conversionService;


    public List<Consultation> getAllConsultations() {
        try {
            return consultationRepository.findAll();

        } catch (Exception e) {

            throw new ConsultationException("Nu exista tabela consultatii", HttpStatus.NOT_FOUND);
        }

    }

    public Consultation getConsultationById(String id) {
        Optional<Consultation> cons = consultationRepository.findById(id);
        if (cons.isPresent()) {
            return cons.get();
        } else {
            // daca nu exista colectia CONSULTATIONS din mongo atunci e not_found
            throw new ConsultationException("Nu exista consultatie", HttpStatus.NOT_FOUND);
        }
    }


    public Consultation saveConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    @Override
    public List<Consultation> getAllConsultationsByPatientId(Integer patientId) {
        //return consultationRepository.findAllById_pacientEquals(patientId);
        throw new ConsultationException("Nu exista consultatie", HttpStatus.NOT_FOUND);

    }

    @Override
    public List<Consultation> getAllConsultationsByDoctorId(Integer doctorId) {
        //return consultationRepository.findAllById_doctorEquals(doctorId);
        throw new ConsultationException("Nu exista consultatie", HttpStatus.NOT_FOUND);
    }

    public List<Consultation> findConsByDocPacAndDate(Long doctor, Long pacient, String date) {
        List<Consultation> res = consultationRepository.findAllByPacientAndDoctor(pacient, doctor);

        if (res.isEmpty()) {
            return res;
        }
        List<Consultation> correctDate = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Consultation c : res) {
            String consDate = dateFormat.format(c.getDate()).substring(0, 10);
            String inputDate = date.substring(0, 10);
            System.out.println(consDate + " " + inputDate);
            if (consDate.equals(inputDate)) {
                correctDate.add(c);
            }

        }
        return correctDate;

    }


}
