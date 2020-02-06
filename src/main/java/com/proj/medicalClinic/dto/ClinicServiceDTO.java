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
public class ClinicServiceDTO {
    private Long id;
    private String name;
    private String address;
    private double review;
    private int reviewCount;
    private double service_price;

    public ClinicServiceDTO(Clinic clinic, double price){
        this.id = clinic.getId();
        this.name = clinic.getName();
        this.address = clinic.getAddress();
        this.review = clinic.getReview();
        this.reviewCount = clinic.getReviewCount();
        this.service_price = price;
    }
}
