package com.proj.medicalClinic.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationRequestDTO {

    private long examStart;
    private long doctorId;
    private long operationRoomId;
    private long serviceId;
    private double duration;

}
