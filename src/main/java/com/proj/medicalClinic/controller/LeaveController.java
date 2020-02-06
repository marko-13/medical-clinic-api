package com.proj.medicalClinic.controller;


import com.proj.medicalClinic.dto.LeaveDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.service.LeaveService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/leave", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllLeaves(){
        try {
            List<LeaveDTO> leaveDTOS = leaveService.getAll();
            return new ResponseEntity<>(leaveDTOS, HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/approveLeave/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMINCLINIC')")
    public ResponseEntity<?> approveLeave(@PathVariable Long id, @RequestBody String email){
        try {
            leaveService.approveLeave(id, email);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/denyLeave/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMINCLINIC')")
    public ResponseEntity<?> denyLeave(@PathVariable Long id, @RequestBody DeniedLeave deniedLeave){
        try {
            String email = deniedLeave.email;
            String message = deniedLeave.message;
            leaveService.denyLeave(id, email, "Your request for leave of absence has been denied. \n\nReason:\n" + message);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    static class DeniedLeave {
        public String email;
        public String message;

    }

}
