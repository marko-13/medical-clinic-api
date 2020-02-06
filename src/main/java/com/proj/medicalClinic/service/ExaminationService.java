package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.ExaminationRequestDTO;

public interface ExaminationService {

    void saveNewExamination(ExaminationRequestDTO newExam);
}
