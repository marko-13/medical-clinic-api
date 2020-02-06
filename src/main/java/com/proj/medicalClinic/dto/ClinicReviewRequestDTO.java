package com.proj.medicalClinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicReviewRequestDTO {

    private Date startDate;
    private Date endDate;

}
