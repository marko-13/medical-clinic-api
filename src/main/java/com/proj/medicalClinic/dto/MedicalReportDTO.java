package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.MedicalReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalReportDTO {
    private Long id;
    private String examDescription;
    private List<DiagnosisRegistryDTO> diagnosisRegistry;
    private PrescriptionDTO prescription;

    public MedicalReportDTO(MedicalReport medicalReport) {
        this.id = medicalReport.getId();
        this.examDescription = medicalReport.getExamDescription();
        this.diagnosisRegistry = medicalReport.getDiagnosisRegistry().stream().map(
                d -> new DiagnosisRegistryDTO(d)
        ).collect(Collectors.toList());
        this.prescription = new PrescriptionDTO(medicalReport.getPrescription());
    }
}
