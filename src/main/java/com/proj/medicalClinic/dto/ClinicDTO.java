package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.Clinic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private double review;
    private int reviewCount;

    public ClinicDTO(Clinic clinic){
        this.id = clinic.getId();
        this.name = clinic.getName();
        this.address = clinic.getAddress();
        this.description = clinic.getDescription();
        this.review = clinic.getReview();
        this.reviewCount = clinic.getReviewCount();
    }
}
