package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AppointmentRequestDTO;
import com.proj.medicalClinic.dto.DoctorDTO;
import com.proj.medicalClinic.model.Clinic;
import com.proj.medicalClinic.model.Doctor;

import java.util.List;

public interface DoctorService {

    List<DoctorDTO> getAll();
    DoctorDTO save(Doctor doctorRequest);
    DoctorDTO remove(Long id);
    List<DoctorDTO> getAllAvailableForDate(AppointmentRequestDTO appointmentRequestDTO);

    List<DoctorDTO> getAllAssociatedWithPatient(String patient_email);

    List<DoctorDTO> getAllFromClinicAndIsNotDeleted();

    void review_doctor(Long id, int score);

    // returns list of doctors that cen perform given examination for given date and work in given clinic
    List<DoctorDTO> getAllAvailableForExam(Long clinc_id, Long selected_date, Long service_id);
}
