package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/nurse", produces = MediaType.APPLICATION_JSON_VALUE)
public class NurseController {
    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    AppointmentService appointmentService;

    @RequestMapping(value = "/get-work-schedule")
    @PreAuthorize("hasAuthority('NURSE') || hasAuthority('DOCTOR')")
    public ResponseEntity<?> getWorkSchedule() {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.appointmentService.getAllAppointmentsByMedicalStaffMember(email), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
