package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.DrugsRegistry;
import com.proj.medicalClinic.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugsRepository extends JpaRepository<DrugsRegistry, String> {

    Optional<DrugsRegistry> findByDrugName(String drugName);
    List<DrugsRegistry> findAllByIdIn(List<Long> id);
    DrugsRegistry findByPrescriptions(Prescription prescription);

}
