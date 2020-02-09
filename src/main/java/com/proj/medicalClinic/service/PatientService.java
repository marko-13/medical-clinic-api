package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    List<PatientDTO> getAll();

    int approve_email(int encoded_email, Long timestamp, Long id);
}
