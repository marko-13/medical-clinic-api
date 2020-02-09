package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.PatientDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Patient;
import com.proj.medicalClinic.model.RoleType;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.service.EmailService;
import com.proj.medicalClinic.service.UserConfirmation;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserConfirmationImpl implements UserConfirmation {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<PatientDTO> getNotApprovedUsers() {
        try {
            List<AppUser> users = appUserRepository.findAllByEnabledAndRejected(false, false)
                    .orElseThrow(NotExistsException::new);

            List<PatientDTO> patientsDTO = new ArrayList<>();
            for (AppUser u : users) {
                patientsDTO.add(new PatientDTO((Patient) u));
            }

            return patientsDTO;
        } catch (NotExistsException e) {
            throw e;
        }
    }

    @Override
    public PatientDTO approvePatient(Long id) {
        try {
            Patient patient = (Patient) appUserRepository.findById(id)
                    .orElseThrow(NotExistsException::new);

            patient.setEnabled(true);
            Patient updated = this.appUserRepository.save(patient);

            //encode email
            int pass = patient.getEmail().hashCode();


            try {
                Long my_timestamp = System.currentTimeMillis();
                this.emailService.sendNotificaitionAsync(updated, "\n\nYour account has been approved.<br></br><a href=\"http://localhost:3000/confirm_auth?id="+pass+"&timestamp="+my_timestamp+ "&broj="+ id+"\">Activate</a>");

            }catch( Exception e ){
            }

            return new PatientDTO(updated);
        } catch (NotExistsException e) {
            throw e;
        }
    }

    @Override
    public boolean denyPatient(Long id, String msg) {
        try {
            AppUser appUser = this.appUserRepository.findById(id).orElse(null);
            if (appUser == null) {
                throw new NotExistsException("This patient doesn't exist");
            } else if (appUser.isEnabled()) {
                throw new NotValidParamsException("This patient is already enabled");
            } else if (appUser.getUserRole() != RoleType.PATIENT) {
                throw new NotValidParamsException("This user is not patient");
            } else if (appUser.isRejected()) {
                throw new NotValidParamsException("This patient has already been rejected");
            } else {
                appUser.setEnabled(false);
                appUser.setRejected(true);
                this.appUserRepository.save(appUser);
                //this.appUserRepository.delete(appUser);
                try {
                    this.emailService.sendNotificaitionAsync(appUser, "\n\nYour account was denied access." + "\n\nReason:\n" + msg);
                }catch( Exception e ){
                }
                return true;
            }
        } catch (NotExistsException e) {
            throw e;
        }
    }


}
