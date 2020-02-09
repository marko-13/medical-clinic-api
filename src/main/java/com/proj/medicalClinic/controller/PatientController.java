package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.*;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/patient", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private UserConfirmation userConfirmation;

    @Autowired
    private MedicalStaffService medicalStaffService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ClinicService clinicService;

    @RequestMapping(value = "/getMedicalStaffByPatient/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllByPatient(@PathVariable Long id){
        try {
            List<AppUserDTO> medicalStaffDTOS = medicalStaffService.getAllStaffByPatientId(id);
            return new ResponseEntity<>(medicalStaffDTOS, HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(){
        try {
            List<PatientDTO> patientDTOS = patientService.getAll();
            return new ResponseEntity<>(patientDTOS, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Patients not found.", HttpStatus.NOT_FOUND);
        }
    }

    //Get all doctors associated with patient
    @RequestMapping(value = "/review-doctors", method = RequestMethod.GET)
    public ResponseEntity<?> getDoctorsByPatient(){
        try {
            String patient_email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            List<DoctorDTO> doctors = doctorService.getAllAssociatedWithPatient(patient_email);
            return new ResponseEntity<>(doctors, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //Get all clinics associated with patient(clinics the patient has visited)
    @RequestMapping(value = "/review-clinics", method = RequestMethod.GET)
    public ResponseEntity<?> getClinicsByPatient(){
        try{
            String patient_email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            List<ClinicDTO> clinics = clinicService.getAllAssociatedWithPatient(patient_email);
            return new ResponseEntity<>(clinics, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    // When patient clicks on activation link, allow him to signin
    @RequestMapping(value = "/approvedemail/{id}/{timestamp}/{number}", method = RequestMethod.GET)
    public ResponseEntity<?> patient_approved_email(@PathVariable int id, @PathVariable Long timestamp,
                                                    @PathVariable Long number){
        try{
            int flag = patientService.approve_email(id, timestamp, number);
            if(flag == 1) {

                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Time expired", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            return new ResponseEntity<>("Poruka", HttpStatus.BAD_REQUEST);
        }
    }
}
