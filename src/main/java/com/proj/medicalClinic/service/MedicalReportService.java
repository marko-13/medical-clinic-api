package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.SimplifiedMedicalReportDTO;
import org.springframework.stereotype.Service;

@Service
public interface MedicalReportService {

    void modifyMedicalReport(SimplifiedMedicalReportDTO simplifiedMedicalReportDTO);

    void addMedicalReport(SimplifiedMedicalReportDTO simplifiedMedicalReportDTO);
}
