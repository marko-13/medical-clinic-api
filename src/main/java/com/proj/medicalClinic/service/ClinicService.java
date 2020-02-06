package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.ClinicDTO;
import com.proj.medicalClinic.dto.ClinicServiceDTO;
import com.proj.medicalClinic.model.Clinic;

import java.util.List;

public interface ClinicService {

    List<ClinicDTO> getAllClinics ();

    ClinicDTO addNewClinic(ClinicDTO clinicDTO, String email);

    ClinicDTO getClinicByAdmin(Long adminId);

    ClinicDTO save(ClinicDTO clinic);

    List<ClinicDTO> getClinicsOfAdminClinicalCenter(String email);

    List<ClinicDTO> getAllAssociatedWithPatient(String patient_email);

    void review_clinic(Long id, int score);

    // Returns list of clinics where its possible to get selected service(appropriate doctors exist)
    // and where are appointemnts available for selected date
    List<ClinicServiceDTO> findCorresponding(Long service_id, Long appointment_date, double min_clinic_score);
}
