package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.DrugsRegistry;
import com.proj.medicalClinic.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private Long id;
    private boolean approved;
    private List<DrugsRegistry> drugs;

    public PrescriptionDTO(Prescription prescription){
        this.id = prescription.getId();
        this.approved = prescription.getApproved();
        this.drugs = prescription.getDrugsRegistry();
    }
}
