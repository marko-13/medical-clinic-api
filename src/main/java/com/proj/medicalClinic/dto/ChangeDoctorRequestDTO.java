package com.proj.medicalClinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDoctorRequestDTO {

    private long appId;
    private long roomId;
    private long doctorId;

}
