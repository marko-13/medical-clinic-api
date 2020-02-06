package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {

    private Long id;
    private double price;
    private String serviceType;

    public ServiceDTO(Service service) { this(service.getId(), service.getPrice(), service.getType()); }
}
