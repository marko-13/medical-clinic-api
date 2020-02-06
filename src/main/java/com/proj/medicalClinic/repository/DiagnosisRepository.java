package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.DiagnosisRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisRegistry, String> {

    Optional<DiagnosisRegistry> findByDiagnosisName(String diagnosisName);
    List<DiagnosisRegistry> findAllByIdIn(List<Long> id);

}
