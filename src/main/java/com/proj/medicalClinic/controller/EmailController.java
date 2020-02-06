package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/email")
public class EmailController {

    //private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String getNew(Model model) {
        model.addAttribute("user", new AppUser());
        return "registration";
    }

    @PostMapping("/async")
    public ResponseEntity<?> signUpAsync(AppUser user, String msg){

        //slanje emaila
        try {
            emailService.sendNotificaitionAsync(user, msg);
        }catch( Exception e ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);    }

}
