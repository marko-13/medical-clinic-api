package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.DiagnosisRegistryDTO;

import java.util.List;

public interface DiagnosisRegistryService {

    List<DiagnosisRegistryDTO> getAllDiagnosis(String email);

    DiagnosisRegistryDTO addDiagnosis(DiagnosisRegistryDTO diagnosisRegistryDTO, String email);
}
