package com.proj.medicalClinic.repository;
import com.proj.medicalClinic.model.Nurse;
import com.proj.medicalClinic.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<List<Prescription>> findAllByNurseAndApprovedIsTrue(Nurse nurse);

    Optional<List<Prescription>> findAllByNurseAndApprovedIsFalse(Nurse nurse);
}
