package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.MedicalHistory;
import com.proj.medicalClinic.model.MedicalReport;
import com.proj.medicalClinic.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    List<MedicalReport> findAllByExamination(Examination examination);
    List<MedicalReport> findAllByPrescription(Prescription prescription);
    MedicalReport findByExamination(Examination examination);
    List<MedicalReport> findAllByMedicalHistory(MedicalHistory medicalHistory);
    Optional<MedicalReport> findAllById(Long id);

    @Modifying
    @Query(
            value = "INSERT INTO medical_report (exam_description, medical_history_id, prescription_id, examination_id) VALUES (?1, ?2, ?3, ?4)",
            nativeQuery = true)
    void saveMedicalReport(String examDescription, Long medicalHistoryId, Long prescriptionId, Long examinationId);
}
