package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AdminClinicDTO;
import com.proj.medicalClinic.model.AdminClinic;

public interface AdminClinicService {
    AdminClinicDTO save(AdminClinicDTO adminClinicDTO, String email);
}
