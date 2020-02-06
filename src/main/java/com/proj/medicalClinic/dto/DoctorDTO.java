package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private String name;
    private String lastname;
    private String email;
    private double review;
    private int reviewCount;
    private int shift;
    private String address;
    private String city;
    private String state;
    private String mobile;
    private long id;

    public DoctorDTO(Doctor d){
        this.name = d.getName();
        this.lastname = d.getLastName();
        this.email = d.getEmail();
        this.review = d.getReview();
        this.shift = d.getShift();
        this.address = d.getAdress();
        this.city = d.getCity();
        this.state = d.getState();
        this.mobile = d.getMobile();
        this.id = d.getId();
        this.reviewCount = d.getReviewCount();
    }

}
