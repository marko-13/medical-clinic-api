package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.ServiceDTO;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Service;

import java.util.Date;
import java.util.List;

public interface ServiceService {

    List<ServiceDTO> getAllFromClinic();

    ServiceDTO save(ServiceDTO service);

    ServiceDTO remove(Long serviceId);

    ServiceDTO edit(ServiceDTO service);

    List<ServiceDTO> getAllFromDoctor(long doctorId);

    // vraca listu serviceDTO-a gde su svi servisi koji nisu obrisani
    List<ServiceDTO> getAllNotDeleted();

    List<ServiceDTO> getAllHeldAndFromOneYearAndFromClinic();
}
