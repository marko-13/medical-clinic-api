package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.MedicalHistory;
import com.proj.medicalClinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    Optional<MedicalHistory> findByPatient(Patient patient);
    Optional<MedicalHistory> findByPatientId(Long patientId);
}
