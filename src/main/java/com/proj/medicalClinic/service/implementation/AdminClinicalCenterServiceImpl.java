package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AdminClinicCenterDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AdminClinicalCenterRepository;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.ClinicalCenterRepository;
import com.proj.medicalClinic.service.AdminClinicalCenterService;
import com.proj.medicalClinic.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdminClinicalCenterServiceImpl implements AdminClinicalCenterService {
    @Autowired
    private AdminClinicalCenterRepository adminClinicalCenterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AppUserRepository appUserRepository;

    public AdminClinicCenterDTO save(AdminClinicCenterDTO adminClinicCenterDTO, String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email).orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter)) {
                throw new NotValidParamsException("Only admin of the clinical center can add new admin of the clinical center");
            }

            Optional<AppUser> checkExistingUsers = appUserRepository.findByEmail(adminClinicCenterDTO.getEmail());

            if (checkExistingUsers.isPresent()) {
                throw new NotValidParamsException("User with email '" + adminClinicCenterDTO.getEmail() + "' already exists. Choose another email.");
            }

            AdminClinicalCenter adminClinicalCenterRequest = new AdminClinicalCenter();
            ClinicalCenter designatedClinicalCenter = ((AdminClinicalCenter) user).getClinicalCenter();

            adminClinicalCenterRequest.setName(adminClinicCenterDTO.getName());
            adminClinicalCenterRequest.setLastName(adminClinicCenterDTO.getLastName());
            adminClinicalCenterRequest.setAdress(adminClinicCenterDTO.getAddress());
            adminClinicalCenterRequest.setCity(adminClinicCenterDTO.getCity());
            adminClinicalCenterRequest.setEmail(adminClinicCenterDTO.getEmail());
            adminClinicalCenterRequest.setLastName(adminClinicCenterDTO.getLastName());
            adminClinicalCenterRequest.setMobile(adminClinicCenterDTO.getMobile());
            adminClinicalCenterRequest.setPassword(this.passwordEncoder.encode(adminClinicCenterDTO.getName()));
            adminClinicalCenterRequest.setState(adminClinicCenterDTO.getState());
            adminClinicalCenterRequest.setClinicalCenter(designatedClinicalCenter);
            adminClinicalCenterRequest.setUserRole(RoleType.ADMINCLINICALCENTER);
            adminClinicalCenterRequest.setEnabled(true);
            adminClinicalCenterRequest.setDeleted(false);
            adminClinicalCenterRequest.setRejected(false);

            List<Authority> auth = authorityService.findByName(adminClinicalCenterRequest.getUserRole().name());
            adminClinicalCenterRequest.setAuthorities(auth);
            adminClinicalCenterRepository.save(adminClinicalCenterRequest);

            return new AdminClinicCenterDTO(adminClinicalCenterRequest);
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

}
