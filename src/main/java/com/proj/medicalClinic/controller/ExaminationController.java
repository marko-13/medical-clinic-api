package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.ExaminationRequestDTO;
import com.proj.medicalClinic.dto.OperationRoomDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/examination", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;


    @RequestMapping(value = "/saveExamination", method = RequestMethod.POST)
    public ResponseEntity<?> getAllFromClinic(@RequestBody ExaminationRequestDTO newExam) {
        try {
            System.out.println(newExam.getDoctorId());
            System.out.println(newExam.getDuration());
            System.out.println(newExam.getExamStart());
            System.out.println(newExam.getOperationRoomId());
            System.out.println(newExam.getServiceId());
            examinationService.saveNewExamination(newExam);
            return new ResponseEntity<>("Uspesno dodavanje exama", HttpStatus.OK);
        } catch (NotExistsException e) {
            return new ResponseEntity<>("Greska pri dodavanju exama", HttpStatus.NOT_FOUND);
        }
    }
}
