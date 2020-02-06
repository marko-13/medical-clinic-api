package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AppUserDTO;

import java.util.List;
import java.util.Optional;

public interface MedicalStaffService {

    List<AppUserDTO> getAllStaffByPatientId(Long id);
}
