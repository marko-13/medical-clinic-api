package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.dto.ServiceDTO;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByDoctors(Doctor doctor);

    @Query(
            value = "SELECT * "
                    + "FROM appointment a, service s "
                    + "WHERE s.id = a.service_id "
                    + "AND a.held = true AND s.deleted = false AND a.clinic_id = ?3 "
                    + "AND a.date between ?1 AND ?2 ORDER BY a.date",
            nativeQuery = true)
    List<Service> findAllHeldAndNotDeletedAndFromBeforeOneYearAndFromClinic(Date from, Date to, long clinicId);

    @Query(
            value = "SELECT s.id, s.price, s.service_type, cs.clinic_id, s.deleted FROM service as s, clinics_services as cs where s.id = cs.service_id and cs.clinic_id = ?1",
            nativeQuery = true)
    List<Service> findAllByClinic(Long id);

    // upit koji vraca sve service(usluge) koji nisu obrisani, kojima je deleted na false
    @Query(
            value = "SELECT s.id, s.deleted, s.price, s.service_type FROM service as s where s.deleted = false",
            nativeQuery = true)
    List<Service> findAllWhereDeletedFalse();
}
