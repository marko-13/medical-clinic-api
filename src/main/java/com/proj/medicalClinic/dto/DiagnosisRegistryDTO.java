package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.DiagnosisRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisRegistryDTO {
    private Long id;
    private String diagnosisName;

    public DiagnosisRegistryDTO(DiagnosisRegistry diagnosisRegistry) {
        this.id = diagnosisRegistry.getId();
        this.diagnosisName = diagnosisRegistry.getDiagnosisName();
    }

}
