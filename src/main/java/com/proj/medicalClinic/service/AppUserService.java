package com.proj.medicalClinic.service;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.RoleType;
import com.proj.medicalClinic.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AppUserService{

     List<AppUser> findAll();

     List<AppUser> findByUserRole(RoleType role);

     AppUser findByEmail(String email);

     AppUser save(AppUser appUser);

     void updateUser(AppUser appUser);
}
