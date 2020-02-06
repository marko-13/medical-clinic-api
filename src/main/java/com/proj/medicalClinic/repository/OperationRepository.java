package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findAllByDoctorsContaining(Doctor doctor);

    List<Operation> findAllByDateBetween(Date d1, Date d2);

    List<Operation> findAll();

}
