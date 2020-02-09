package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<List<Appointment>> findAllByOperationRoomId(Long id);
    Optional<List<Appointment>> findAllByPatientId(Long id);
    List<Appointment> findByServiceId(Long serviceId);
    List<Appointment> findAllByDateBetweenAndOperationRoomIsNotNull(Date start, Date end);
    List<Appointment> findAllByDateBetween(Date start, Date end);
    List<Appointment> findAllByOperationRoomIsNull();


    @Query(
            value = "select * from appointment where held = true and clinic_id = ?1 and date between ?2 and ?3",
            nativeQuery = true)
    Optional<List<Appointment>> findAllHeldAndFromClinicBetweenNowAndEndDate(Long clinicId, Date start, Date end);

    @Query(
            value = "select * from appointment where clinic_id = ?1 and operation_room_id IS NULL;",
            nativeQuery = true)
    Optional<List<Appointment>> findAllAppointmentRequests(Long clinicId);
  
    @Query(
            value = "SELECT ap.version, ap.type, ap.confirmed, ap.id, ap.date, ap.duration, ap.fast, ap.clinic_id, ap.operation_room_id, ap.patient_id, ap.service_id, ap.nurse_id, ap.held FROM appointment as ap where ap.nurse_id = ?1 and ap.confirmed = 2",
            nativeQuery = true)
    List<Appointment> findAllByNurse(Long id);

    @Transactional
    @Query(
            value = "SELECT ap.version, ap.type,ap.confirmed, ap.held, ap.id, ap.date, ap.duration, ap.fast, ap.clinic_id, ap.operation_room_id, ap.patient_id, ap.service_id, ap.nurse_id FROM appointment as ap where ap.id = ?1",
            nativeQuery = true)
    Optional<Appointment> findById(Long id);


    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE appointment SET operation_room_id = ?1 WHERE appointment.id = ?2",
            nativeQuery = true
    )
    void saveNative(long roomId, long appointmentId);

    // find all by clinic id and date greater than today and where patient is null
    //FAST EXAMS
    List<Appointment> findAllByClinicIdAndDateAfterAndPatientId(Long clinic_id, Date date, Long patient_id);
}
