package com.proj.medicalClinic.dto;


import com.proj.medicalClinic.model.OperationRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationRoomDTO {

    private Long roomId;
    private String name;
    private int number;
    private long clinicId;

    public OperationRoomDTO(OperationRoom or){
        this(or.getId(), or.getName(),or.getNumber(), or.getClinic().getId());
    }

}
