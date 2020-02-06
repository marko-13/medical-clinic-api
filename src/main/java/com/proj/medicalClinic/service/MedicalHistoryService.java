package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.MedicalHistoryDTO;
import org.springframework.stereotype.Service;

@Service
public interface MedicalHistoryService {

    MedicalHistoryDTO getMedicalHistory(String email);
    MedicalHistoryDTO getMedicalHistoryByPatientId(Long patientId, String email);
}
