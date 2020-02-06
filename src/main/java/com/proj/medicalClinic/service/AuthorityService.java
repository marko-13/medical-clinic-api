package com.proj.medicalClinic.service;

import com.proj.medicalClinic.model.Authority;

import java.util.List;


public interface AuthorityService {
    List<Authority> findById(Long id);
    List<Authority> findByName(String name);
}