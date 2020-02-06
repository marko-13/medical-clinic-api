package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.ClinicDTO;
import com.proj.medicalClinic.dto.ClinicServiceDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.ClinicService;
import com.proj.medicalClinic.service.implementation.CustomUserDetailsServiceImpl;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/clinics", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClinicController {
    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AppUserService userService;

    @Autowired
    private ClinicService clinicService;

    @RequestMapping(value = "/get")
    public ResponseEntity<?> getAllClinics() {

        return new ResponseEntity<>(this.clinicService.getAllClinics(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getByAdmin/{id}")
    public ResponseEntity<?> getClinicByAdminId(@PathVariable long id){
        try{
            ClinicDTO clinicDTO = clinicService.getClinicByAdmin(id);
            return new ResponseEntity<>(clinicDTO, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Klinika nije pronadjena!", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/get-all-clinical-center-clinics")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> getClinicsOfAdminClinicalCenter(){
        try{
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(clinicService.getClinicsOfAdminClinicalCenter(email), HttpStatus.OK);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/save")
    public ResponseEntity<?> saveClinic(@RequestBody ClinicDTO clinicRes){
        try {
            ClinicDTO clinicDTO = clinicService.save(clinicRes);
            return new ResponseEntity<>(clinicDTO, HttpStatus.OK);
        }catch (NotExistsException e){
            return new ResponseEntity<>("Klinika nije pronadjena!", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/reviewed/{id}/{score}")
    public ResponseEntity<?> recieve_review(@PathVariable Long id, @PathVariable int score){
        try{
            clinicService.review_clinic(id, score);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }catch(NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findCorrespondingClinics/{service_id}/{appointment_date}/{min_clinic_score}")
    public ResponseEntity<?> findCorespondingClinics(@PathVariable Long service_id,
                                                     @PathVariable Long appointment_date, @PathVariable double min_clinic_score){
        try{
            List<ClinicServiceDTO> ret = clinicService.findCorresponding(service_id, appointment_date, min_clinic_score);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
