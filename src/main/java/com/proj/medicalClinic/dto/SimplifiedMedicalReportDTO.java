package com.proj.medicalClinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedMedicalReportDTO {
    private String examDescription;
    private Long medicalReportID;
    private List<Long> selectedDiagnosis;
    private List<Long> selectedDrugs;
    private Long examID;
}
