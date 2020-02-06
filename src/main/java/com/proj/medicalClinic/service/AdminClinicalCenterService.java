package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AdminClinicCenterDTO;

public interface AdminClinicalCenterService {
    AdminClinicCenterDTO save(AdminClinicCenterDTO adminClinicCenterDTO, String email);
}
