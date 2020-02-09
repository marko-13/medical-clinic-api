package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Clinic;
import com.proj.medicalClinic.model.ClinicalCenter;
import com.proj.medicalClinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    List<Clinic> findAll();

    List<Clinic> findAllByNameAndAddress(String name, String address);

    Optional<Clinic> findClinicById(Long id);

    List<Clinic> findAllByClinicalCenter(ClinicalCenter clinicalCenter);

    @Query(
            value = "SELECT clinic.version, clinic.Id, clinic.Name, clinic.Address, clinic.Description, clinic.review, clinic.review_count, clinic.clinical_center_id" +
                    " FROM clinic JOIN app_user a ON clinic.Id = a.clinic_id WHERE a.id = ?1",
            nativeQuery = true)
    Optional<Clinic> findByDoctorId(Long id);

    //upit koji vraca sve klinike na kojima je pacijent ciji je id prosledjen bio
    @Query(
            value = "SELECT c.version, c.id, c.name, c.address, c.description, c.review, c.review_count, c.clinical_center_id" +
                    " FROM clinic as c, clinics_patients as cp WHERE c.id = cp.clinic_id and cp.patient_id = ?1",
            nativeQuery = true)
    List<Clinic> findAllByPatientId(Long id);

    // upit koji vraca sve klinike na kojima zeljeni pregled moze da se obavi
    @Query(
            value = "SELECT c.version, c.id, c.name, c.address, c.description, c.review, c.review_count, c.clinical_center_id" +
                    " FROM clinic as c, clinics_services as cs WHERE c.id = cs.clinic_id and cs.service_id = ?1",
            nativeQuery = true)
    List<Clinic> findByServiceId(Long id);
}
