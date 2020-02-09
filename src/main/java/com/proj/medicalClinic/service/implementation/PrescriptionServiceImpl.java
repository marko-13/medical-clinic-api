package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.PrescriptionDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.DrugsRegistry;
import com.proj.medicalClinic.model.Nurse;
import com.proj.medicalClinic.model.Prescription;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.DrugsRepository;
import com.proj.medicalClinic.repository.PrescriptionRepository;
import com.proj.medicalClinic.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PrescriptionServiceImpl implements PrescriptionService {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DrugsRepository drugsRepository;

    @Override
    public List<PrescriptionDTO> getApprovedPrescriptions (String email) {
        try {
            Nurse nurse = (Nurse) userDetailsService.loadUserByUsername(email);
            if (nurse == null) {
                throw new NotExistsException("Nurse doesn't exists");
            }

            List<Prescription> prescriptions = prescriptionRepository.findAllByNurseAndApprovedIsTrue(nurse)
                    .orElseThrow(NotExistsException::new);

            List<PrescriptionDTO> prescriptionsDTO = new ArrayList<>();
            for (Prescription p : prescriptions) {
                prescriptionsDTO.add(new PrescriptionDTO(p));
            }

            return prescriptionsDTO;
        } catch(NotExistsException e) {
            throw e;
        } catch(Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<PrescriptionDTO> getNotApprovedPrescriptions (String email) {
        try {
            Nurse nurse = (Nurse) userDetailsService.loadUserByUsername(email);
            if (nurse == null) {
                throw new NotExistsException("Nurse doesn't exists");
            }

            List<Prescription> prescriptions = prescriptionRepository.findAllByNurseAndApprovedIsFalse(nurse)
                    .orElse(new ArrayList<>());

            List<PrescriptionDTO> prescriptionsDTO = new ArrayList<>();
            for (Prescription p : prescriptions) {
                System.out.println("Prescription id " + p.getId());
                if(p.getDrugsRegistry().isEmpty()) {
                    System.out.println("Jeste prazno");
                }



                //if (!drugsRegistry.isEmpty()) {
                prescriptionsDTO.add(new PrescriptionDTO(p));
                //}
            }

            return prescriptionsDTO;
        } catch(NotExistsException e) {
            throw e;
        } catch(Exception ex) {
            throw ex;
        }

    }

    @Override
    public PrescriptionDTO approvePrescription (String email, Long id) {
        try {
            Prescription prescription = this.prescriptionRepository.findById(id)
                    .orElseThrow(NotExistsException::new);
            Nurse nurse = (Nurse) appUserRepository.findById(prescription.getNurse().getId())
                    .orElseThrow(NotExistsException::new);

            if (email.equals(nurse.getEmail())) {
                prescription.setApproved(true);
                Prescription updated = this.prescriptionRepository.save(prescription);
                return new PrescriptionDTO(updated);
            } else {
                throw new NotValidParamsException("Prescription is not assigned to this nurse");
            }
        } catch(NotExistsException e) {
            throw e;
        } catch(Exception ex) {
            throw ex;
        }
    }

}
