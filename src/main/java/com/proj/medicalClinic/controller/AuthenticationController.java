package com.proj.medicalClinic.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Patient;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.repository.PatientRepository;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.implementation.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AppUserService userService;

    @Autowired
    PatientRepository patientRepository;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                       HttpServletResponse response) throws AuthenticationException, IOException {

        final Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        // Ubaci username + password u kontext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token
        AppUser user = (AppUser) authentication.getPrincipal();
        String userRole = user.getUserRole().name();

        if (userRole.equals("PATIENT")){

            if(!user.getEnabledPatient()){
                return ResponseEntity.badRequest().body("This user is not yet approved");
            }

        }
        if (user.isEnabled()) {
            String jwt = tokenUtils.generateToken(user.getUsername(), user.getUserRole().name(), user.getName(), user.getLastName(), user.getId(), user.getLastPasswordResetDate() != null);
            int expiresIn = tokenUtils.getExpiredIn();

            // Vrati token kao odgovor na uspesno autentifikaciju
            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
        } else {
            return ResponseEntity.badRequest().body("This user is not yet approved");
        }
    }

    @RequestMapping(method = POST, value = "/register")
    public ResponseEntity<?> addUser(@RequestBody AppUser appUser, UriComponentsBuilder ucBuilder) {
        try {
            AppUser existUser = this.userService.findByEmail(appUser.getUsername());
            if (existUser != null) {
                throw new ResourceConflictException(appUser.getId(), "Email already exists");
            }
        }
        catch(NotExistsException e){
            AppUser user = this.userService.save(appUser);
            HttpHeaders headers = new HttpHeaders();

            //Sta ovo tacno radi?
            headers.setLocation(ucBuilder.path("/api/user/{userId}").buildAndExpand(user.getId()).toUri());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) {

        String token = tokenUtils.getToken(request);
        String username = this.tokenUtils.getUsernameFromToken(token);
        AppUser user = (AppUser) this.userDetailsService.loadUserByUsername(username);

        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = tokenUtils.refreshToken(token);
            int expiresIn = tokenUtils.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.badRequest().body(userTokenState);
        }
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:3000")
    //hasrole ??
    public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
        userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.accepted().body(result);
    }

    static class PasswordChanger {
        public String oldPassword;
        public String newPassword;
    }
}