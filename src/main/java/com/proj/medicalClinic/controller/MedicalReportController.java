package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.SimplifiedMedicalReportDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/medical-reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalReportController {
    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private MedicalReportService medicalReportService;

    @RequestMapping(value = "/modify")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<?> modifyMedicalReport(@RequestBody SimplifiedMedicalReportDTO simplifiedMedicalReportDTO) {
        try {
            this.medicalReportService.modifyMedicalReport(simplifiedMedicalReportDTO);
            return new ResponseEntity<>("Successfully modified medical report", HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/add")
    @PreAuthorize("hasAuthority('DOCTOR') || hasAuthority('NURSE') ")
    public ResponseEntity<?> addMedicalReport(@RequestBody SimplifiedMedicalReportDTO simplifiedMedicalReportDTO) {
        try {
            this.medicalReportService.addMedicalReport(simplifiedMedicalReportDTO);
            return new ResponseEntity<>("Successfully added medical report", HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
