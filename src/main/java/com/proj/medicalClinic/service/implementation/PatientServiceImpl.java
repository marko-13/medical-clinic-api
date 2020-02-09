package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.PatientDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.Patient;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.ExaminationRepository;
import com.proj.medicalClinic.repository.PatientRepository;
import com.proj.medicalClinic.service.PatientService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Override
    public List<PatientDTO> getAll() {
        List<Patient> patients = patientRepository.findAll();
        if(patients.isEmpty()){
            throw new NotExistsException();
        }

        List<PatientDTO> patientDTOS = new ArrayList<>();

        for(Patient p : patients){
            patientDTOS.add(new PatientDTO(p));
        }

        return patientDTOS;
    }

    @Override
    public int approve_email(int encoded_email, Long timestamp, Long id) {
        // System.out.println("UDJE OVDEE\n\n");
        System.out.println(id);
        AppUser au = appUserRepository.findById(id).orElseThrow(NotExistsException::new);
        System.out.println("EVO");
        int flag = 1;
        if (System.currentTimeMillis() - timestamp > 86400000L){
            flag = 0;
            return flag;
        }

        //encode email
        int pass = au.getEmail().hashCode();

        System.out.println(pass);
        System.out.println(encoded_email);
        if(!(pass==(encoded_email))){
            System.out.println("PAO KOD POREDJENJA");
            flag = 0;
            return flag;
        }

        au.setEnabled_patient(true);
        List<Patient> patients = patientRepository.findAll();
        if(patients.isEmpty()){
            throw new NotExistsException();
        }
        Patient my_patient = null;
        for(Patient p : patients){
           if (p.getEmail().equals(au.getEmail())){
               my_patient = p;
               break;
           }
        }
        if(my_patient == null){
            throw new NotExistsException();
        }
        my_patient.setEnabled_patient(true);
        patientRepository.save(my_patient);
        System.out.println("DOSAO DO KRAJA");
        return flag;
    }

    @Override
    public void confirm_exam(int broj, Long app_id) {
        if(broj == 2){
            Examination ex = examinationRepository.findById(app_id).orElseThrow(NotExistsException::new);
//            if(ex.getConfirmed() !=1 || ex.getConfirmed()!=0){
//                throw new NotValidParamsException();
//            }
            ex.setConfirmed(2);
            examinationRepository.save(ex);
            return;
        }
        if(broj == 3){
            Examination ex = examinationRepository.findById(app_id).orElseThrow(NotExistsException::new);
//            if(ex.getConfirmed() !=1 || ex.getConfirmed()!=0){
//                throw new NotValidParamsException();
//            }
            ex.setConfirmed(3);
            examinationRepository.save(ex);
            return;
        }

    }
}
