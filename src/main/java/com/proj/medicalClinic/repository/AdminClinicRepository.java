package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.AdminClinic;
import com.proj.medicalClinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminClinicRepository extends JpaRepository<AdminClinic, Long> {

    List<AdminClinic> findAllByClinic(Clinic clinic);
}
