package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.DrugsRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugsRegistryDTO {
    private Long id;
    private String drugName;

    public DrugsRegistryDTO(DrugsRegistry drugsRegistry) {
        this.id = drugsRegistry.getId();
        this.drugName = drugsRegistry.getDrugName();
    }
}
