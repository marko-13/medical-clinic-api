package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.Nurse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NurseDTO {

    private String name;
    private String lastname;
    private String email;
    private int shift;
    private String address;
    private String city;
    private String state;
    private String mobile;

    public NurseDTO (Nurse n){
        this.name = n.getName();
        this.lastname = n.getLastName();
        this.email = n.getEmail();
        this.shift = n.getShift();
        this.address = n.getAdress();
        this.city = n.getCity();
        this.state = n.getState();
        this.mobile = n.getMobile();
    }

}
