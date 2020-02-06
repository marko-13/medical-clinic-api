package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.ClinicalCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicalCenterRepository extends JpaRepository<ClinicalCenter, Long> {

    List<ClinicalCenter> findAll();

    @Override
    Optional<ClinicalCenter> findById(Long aLong);
}
