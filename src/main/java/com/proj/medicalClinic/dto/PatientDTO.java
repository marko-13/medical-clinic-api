package com.proj.medicalClinic.dto;


import com.proj.medicalClinic.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private String name;
    private String lastname;
    private String email;
    private String jmbg;
    private Long id;
    private boolean enabled;
    private String address;
    private String city;
    private String state;
    private String mobile;

    public PatientDTO(Patient p){
        id = p.getId();
        enabled = p.isEnabled();
        name = p.getName();
        lastname = p.getLastName();
        email = p.getEmail();
        jmbg = p.getJMBG();
        this.address = p.getAdress();
        this.city = p.getCity();
        this.state = p.getState();
        this.mobile = p.getMobile();
    }

}
