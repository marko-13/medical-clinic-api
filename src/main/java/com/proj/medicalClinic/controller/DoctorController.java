package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.*;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppointmentService;
import com.proj.medicalClinic.service.DoctorService;
import com.proj.medicalClinic.service.StartExamService;
import com.proj.medicalClinic.service.implementation.DoctorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/doctor", produces = MediaType.APPLICATION_JSON_VALUE)
public class DoctorController {

    @Autowired
    private DoctorServiceImpl doctorService;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    StartExamService startExamService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDoctors() {
        List<DoctorDTO> doctorDTOS = doctorService.getAll();
        return new ResponseEntity<>(doctorDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        DoctorDTO doctorDTO = doctorService.save(doctor);
        return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> removeDoctor(@PathVariable Long id){
        try{
            DoctorDTO doctorDTO = doctorService.remove(id);
            return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (ResourceConflictException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    // Post za vrsenje ocenjivanja doktora
    @RequestMapping(value = "/reviewed/{id}/{score}", method = RequestMethod.POST)
    public ResponseEntity<?> recieve_review(@PathVariable Long id,@PathVariable int score){
        try{
            doctorService.review_doctor(id, score);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }catch(NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getAllAvailable", method = RequestMethod.POST)
    public ResponseEntity<?> getAllAvailable(@RequestBody AppointmentRequestDTO appointmentRequestDTO){
        try {
            List<DoctorDTO> doctorDTOS = doctorService.getAllAvailableForDate(appointmentRequestDTO);
            return new ResponseEntity<>(doctorDTOS, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Nije nasao doktore", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/getCurrent/{appointmentId}", method = RequestMethod.POST)
    public ResponseEntity<?> getCurrent(@PathVariable Long appointmentId){
        try {
            DoctorDTO doctorDTO = doctorService.getCurrent(appointmentId);
            return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/start-exam/{patientId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<?> getMedicalHistoryStartExamination(@PathVariable Long patientId) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.startExamService.getMedicalHistoryStartExamination(email, patientId), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/finish-exam/{patientId}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<?> saveMedicalHistoryFinishExam(@PathVariable Long patientId, @RequestBody StartExamDTO startExamDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            this.startExamService.saveMedicalHistoryFinishExamination(email, startExamDTO, patientId);
            return new ResponseEntity<>("Successfully finished the examination", HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // returns all doctors that can perform selected service on a selected date and clinic
    @RequestMapping(value = "/getAllAvailableForExam/{clinc_id}/{selected_date}/{service_id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllAvailableForExam(@PathVariable Long clinc_id, @PathVariable Long selected_date,
                                                    @PathVariable Long service_id){

        try{
            List<DoctorDTO> doctorDTOS = doctorService.getAllAvailableForExam(clinc_id, selected_date, service_id);
            return new ResponseEntity<>(doctorDTOS, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Nije nasao doktore" + e.getMessage(), HttpStatus.NOT_FOUND);
        }
      }
  
    @RequestMapping(value = "/getAllFromClinicAndNotDeleted", method = RequestMethod.GET)
    public ResponseEntity<?> getAllFromClinicAndAreNotDeleted() {
        try {
            List<DoctorDTO> doctorDTOS = doctorService.getAllFromClinicAndIsNotDeleted();
            return new ResponseEntity<>(doctorDTOS, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Greska pri trazenju doktora", HttpStatus.NOT_FOUND);
        }
    }
}
