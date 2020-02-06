package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.AdminClinicalCenter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminClinicCenterDTO {

    private String name;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String state;
    private String mobile;

    public AdminClinicCenterDTO(AdminClinicalCenter acc){
        this.name = acc.getName();
        this.lastName = acc.getLastName();
        this.email = acc.getEmail();
        this.address = acc.getAdress();
        this.city = acc.getCity();
        this.state = acc.getState();
        this.mobile = acc.getMobile();
    }

}
