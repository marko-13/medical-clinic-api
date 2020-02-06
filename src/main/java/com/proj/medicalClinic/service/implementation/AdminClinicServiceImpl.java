package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AdminClinicDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AdminClinicRepository;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.ClinicRepository;
import com.proj.medicalClinic.service.AdminClinicService;
import com.proj.medicalClinic.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class AdminClinicServiceImpl implements AdminClinicService {
    @Autowired
    private AdminClinicRepository adminClinicRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AppUserRepository appUserRepository;

    public AdminClinicDTO save(AdminClinicDTO adminClinicDTO, String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter)) {
                throw new NotValidParamsException("Only admin of the clinical center can add new Clinic's administrator");
            }

            Optional<AppUser> checkExistingUsers = appUserRepository.findByEmail(adminClinicDTO.getEmail());

            if (checkExistingUsers.isPresent()) {
                throw new NotValidParamsException("User with email '" + adminClinicDTO.getEmail() + "' already exists. Choose another email.");
            }

            AdminClinic adminClinicRequest = new AdminClinic();
            Clinic deisgnatedClinic = clinicRepository.findClinicById(adminClinicDTO.getClinicID()).orElseThrow(NotExistsException::new);

            adminClinicRequest.setName(adminClinicDTO.getName());
            adminClinicRequest.setLastName(adminClinicDTO.getLastName());
            adminClinicRequest.setAdress(adminClinicDTO.getAddress());
            adminClinicRequest.setCity(adminClinicDTO.getCity());
            adminClinicRequest.setEmail(adminClinicDTO.getEmail());
            adminClinicRequest.setLastName(adminClinicDTO.getLastName());
            adminClinicRequest.setMobile(adminClinicDTO.getMobile());
            adminClinicRequest.setPassword(this.passwordEncoder.encode(adminClinicDTO.getName()));
            adminClinicRequest.setState(adminClinicDTO.getState());
            adminClinicRequest.setClinic(deisgnatedClinic);
            adminClinicRequest.setUserRole(RoleType.ADMINCLINIC);
            adminClinicRequest.setEnabled(true);
            adminClinicRequest.setDeleted(false);
            adminClinicRequest.setRejected(false);

            List<Authority> auth = authorityService.findByName(adminClinicRequest.getUserRole().name());
            adminClinicRequest.setAuthorities(auth);
            adminClinicRepository.save(adminClinicRequest);

            return new AdminClinicDTO(adminClinicRequest);
        } catch (NotValidParamsException | NotExistsException e) {
            throw e;
        }
    }

}
