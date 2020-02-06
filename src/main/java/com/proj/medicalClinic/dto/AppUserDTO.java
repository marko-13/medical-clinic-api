package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AppUserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;

    public AppUserDTO() {

    }

    public AppUserDTO(AppUser appUser){
        this.id = appUser.getId();
        this.email = appUser.getEmail();
        this.firstName = appUser.getName();
        this.lastName = appUser.getLastName();
    }

    public AppUserDTO(Long id, String email, String firstName, String lastName){
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
