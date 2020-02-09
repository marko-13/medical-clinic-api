package com.proj.medicalClinic.repository;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Date;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findAllByDoctorsContainingAndConfirmed(Doctor doctor, int confirmed);

    Optional<Examination> findById(Long id);

    List<Examination> findAllByDateBetween(Date d1, Date d2);

    List<Examination> findAll();

}
