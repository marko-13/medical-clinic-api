package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Leave;
import com.proj.medicalClinic.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findAll();
    Optional<Leave> findById(Long id);

    @Query(
            value = "SELECT * FROM leave l WHERE l.active = true",
            nativeQuery = true
        )
    List<Leave> findAllUnapproved();

    @Query(
            value = "SELECT * FROM leave l WHERE l.date_start < ?1 OR l.date_end > ?2",
            nativeQuery = true
    )
    List<Leave> findByDateBeforeOrAfter(Date d1, Date d2); //dat poc dana i dat zavrsetka dana

    // valjda ce raditi
    List<Leave> findAllByDoctor(Doctor d);

    List<Leave> findAllByNurse(Nurse n);
}
