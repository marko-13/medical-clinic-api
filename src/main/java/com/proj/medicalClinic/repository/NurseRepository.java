package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {
    Nurse findByExaminations(Examination examination);
}
