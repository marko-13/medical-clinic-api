package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Leave;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveDTO {

    private Long leaveID;
    private Date start;
    private Date end;
    private Long userID;
    private String name;
    private String lastname;
    private String email;

    public LeaveDTO(Leave l, NurseDTO nurseDTO){
        this.leaveID = l.getId();
        this.start = l.getDateStart();
        this.end = l.getDateEnd();

        if(l.getNurse() != null){
            this.userID = l.getNurse().getId();
        }else{
            this.userID = l.getDoctor().getId();
        }

        this.name = nurseDTO.getName();
        this.lastname = nurseDTO.getLastname();
        this.email = nurseDTO.getEmail();
    }

    public LeaveDTO(Leave l, DoctorDTO doctorDTO){
        this.leaveID = l.getId();
        this.start = l.getDateStart();
        this.end = l.getDateEnd();

        if(l.getNurse() != null){
            this.userID = l.getNurse().getId();
        }else{
            this.userID = l.getDoctor().getId();
        }

        this.name = doctorDTO.getName();
        this.lastname = doctorDTO.getLastname();
        this.email = doctorDTO.getEmail();
    }

}
