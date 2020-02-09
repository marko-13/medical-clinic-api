package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AppUserDTO;
import com.proj.medicalClinic.dto.DiagnosisRegistryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.RoleType;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.AppointmentRepository;
import com.proj.medicalClinic.repository.ExaminationRepository;
import com.proj.medicalClinic.service.MedicalStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MedicalStaffServiceImpl implements MedicalStaffService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public List<AppUserDTO> getAllStaffByPatientId(Long id) {
        try {
            List<AppUser> medicalStaff = appUserRepository.findByUserRole(RoleType.DOCTOR)
                    .orElseThrow(NotExistsException::new);
            List<AppUser> medicalStaffNurses = appUserRepository.findByUserRole(RoleType.NURSE)
                    .orElseThrow(NotExistsException::new);

            for (AppUser appUser : medicalStaffNurses) {
                medicalStaff.add(appUser);
            }

            List<AppUserDTO> appUserDTO = medicalStaff.stream().map(
                    s -> new AppUserDTO(s)
            ).collect(Collectors.toList());

            return appUserDTO;

        }catch (NotExistsException e){
            throw e;
        }

    }
}
