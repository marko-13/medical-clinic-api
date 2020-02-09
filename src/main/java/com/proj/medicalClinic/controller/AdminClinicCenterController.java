package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.dto.*;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.Patient;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebResult;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/admin-clinic-center", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminClinicCenterController {
    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private UserConfirmation userConfirmation;

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private DiagnosisRegistryService diagnosisRegistryService;

    @Autowired
    private DrugsRegistryService drugsRegistryService;

    @Autowired
    private AdminClinicService adminClinicService;

    @Autowired
    private AdminClinicalCenterService adminClinicalCenterService;

    @RequestMapping(value = "/approve")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> getNotApprovedUsers() {
        return new ResponseEntity<>(this.userConfirmation.getNotApprovedUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/{id}")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> approvePatients(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(this.userConfirmation.approvePatient(id), HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/deny/{id}")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> denyPatients(@PathVariable Long id, @RequestBody String msg) {
        try {
            return new ResponseEntity<>(this.userConfirmation.denyPatient(id, msg), HttpStatus.OK);
        }
        catch(NotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/add-new-clinic")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> addNewClinics(@RequestBody ClinicDTO clinicDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.clinicService.addNewClinic(clinicDTO, email), HttpStatus.OK);
        }
        catch(NotValidParamsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/diagnosis/get-all-diagnosis")
    @PreAuthorize("hasAnyAuthority('ADMINCLINICALCENTER', 'DOCTOR', 'NURSE')")
    public ResponseEntity<?> getAllDiagnosis() {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.diagnosisRegistryService.getAllDiagnosis(email), HttpStatus.OK);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/diagnosis/add-new-diagnosis")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> addNewDiagnosis(@RequestBody DiagnosisRegistryDTO diagnosisRegistryDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.diagnosisRegistryService.addDiagnosis(diagnosisRegistryDTO, email), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/drugs/get-all-drugs")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER') or hasAuthority('DOCTOR') or hasAuthority('NURSE')")
    public ResponseEntity<?> getAllDrugs() {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.drugsRegistryService.getAllDrugs(email), HttpStatus.OK);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/drugs/add-new-drug")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> addNewDrug(@RequestBody DrugsRegistryDTO drugsRegistryDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.drugsRegistryService.addDrug(drugsRegistryDTO, email), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/add-new-clinic-administrator")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> addNewAdminClinic(@RequestBody AdminClinicDTO adminClinicDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.adminClinicService.save(adminClinicDTO, email), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/add-new-administrator-of-clinical-center")
    @PreAuthorize("hasAuthority('ADMINCLINICALCENTER')")
    public ResponseEntity<?> addNewAdminClinic(@RequestBody AdminClinicCenterDTO adminClinicCenterDTO) {
        try {
            String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
            return new ResponseEntity<>(this.adminClinicalCenterService.save(adminClinicCenterDTO, email), HttpStatus.OK);
        } catch (NotValidParamsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
