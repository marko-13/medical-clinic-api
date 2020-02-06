package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.StartExamDTO;

public interface StartExamService {

    StartExamDTO getMedicalHistoryStartExamination(String email, Long patientId);

    void saveMedicalHistoryFinishExamination(String email, StartExamDTO startExamDTO, Long patientId);
}
