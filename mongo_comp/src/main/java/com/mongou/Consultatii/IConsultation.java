package com.mongou.Consultatii;

import java.util.List;

interface IConsultation {
     List<Consultation> getAllConsultations();
     Consultation getConsultationById(String id);
     Consultation saveConsultation(Consultation consultation);

    List<Consultation> getAllConsultationsByPatientId(Integer patientId);

    List<Consultation> getAllConsultationsByDoctorId(Integer doctorId);

     List<Consultation> findConsByDocPacAndDate(Long doctor, Long pacient, String date) ;


    }
