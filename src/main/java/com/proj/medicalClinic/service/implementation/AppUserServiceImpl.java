package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.AuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserServiceImpl implements AppUserService {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Override
    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<AppUser> findByUserRole(RoleType role) {
        return userRepository.findByUserRole(role)
                .orElseThrow(NotExistsException::new);
    }

    @Override
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(NotExistsException::new);
    }

    @Override
    public AppUser save(AppUser userRequest) {
        AppUser u;
        switch (userRequest.getUserRole()) {
            case PATIENT:
                u = new Patient();
                u.setEnabled(true);
                u.setEnabled_patient(false);
                break;
            case NURSE:
                u = new Nurse();
                u.setEnabled(true);
                u.setEnabled_patient(true);
                break;
            case DOCTOR:
                u = new Doctor();
                u.setEnabled(true);
                u.setEnabled_patient(true);
                break;
            case ADMINCLINIC:
                u = new AdminClinic();
                u.setEnabled(true);
                u.setEnabled_patient(true);
                break;
            case ADMINCLINICALCENTER:
                u = new AdminClinicalCenter();
                u.setEnabled(true);
                u.setEnabled_patient(true);
                break;
            default:
                u = new AppUser();
                break;
        }

        u.setEmail(userRequest.getEmail());
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        u.setEnabled(false);
        u.setRejected(false);
        u.setName(userRequest.getName());
        u.setLastName(userRequest.getLastName());
        u.setUserRole(userRequest.getUserRole());
        u.setAdress(userRequest.getAdress());
        u.setCity(userRequest.getCity());
        u.setState(userRequest.getState());
        u.setMobile(userRequest.getMobile());

        List<Authority> auth = authorityService.findByName(userRequest.getUserRole().name());
        u.setAuthorities(auth);
        u = this.userRepository.save(u);
        return u;
    }

    @Override
    public void updateUser(AppUser appUser){
        try {
            Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
            String username = currentUser.getName();

            AppUser user = (AppUser) customUserDetailsService.loadUserByUsername(username);

            user.setName(appUser.getName());
            user.setLastName(appUser.getLastName());
            user.setEmail(appUser.getEmail());
            user.setAdress(appUser.getAdress());
            user.setCity(appUser.getCity());
            user.setState(appUser.getState());
            user.setMobile(appUser.getMobile());

            userRepository.save(user);

        }catch(NotExistsException e) {
            throw e;
        } catch(Exception ex) {
            throw ex;
        }

    }

}
