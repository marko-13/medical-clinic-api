package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.PrescriptionDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Nurse;
import com.proj.medicalClinic.model.Prescription;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.PrescriptionService;
import com.proj.medicalClinic.service.implementation.CustomUserDetailsServiceImpl;
import com.sun.mail.imap.IMAPBodyPart;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/prescriptions", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrescriptionController {
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
    private PrescriptionService prescriptionService;


    @RequestMapping(value = "/approve")
    @PreAuthorize("hasAuthority('NURSE')")
    public ResponseEntity<?> getNotApprovedPrescriptions() {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.prescriptionService.getNotApprovedPrescriptions(email), HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/past")
    @PreAuthorize("hasAuthority('NURSE')")
    public ResponseEntity<?> getPastPrescriptions() {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.prescriptionService.getApprovedPrescriptions(email), HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/approve/{id}")
    @PreAuthorize("hasAuthority('NURSE')")
    public ResponseEntity<?> approvePrescriptions(@PathVariable Long id) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.prescriptionService.approvePrescription(email, id), HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
