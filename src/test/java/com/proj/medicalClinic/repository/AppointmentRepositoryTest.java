package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @After
    public void cleanUp() {
        appointmentRepository.deleteAll();
    }


    @Test
    public void whenFindById_thenReturnNonExisting(){
        // given
        // created in memory database for testing purposes

        // when
        Optional<Appointment> found = appointmentRepository.findById(1000L);

        // then
        assertFalse("Appointment does not exist", found.isPresent());
    }


    @Test
    public void whenFindById_thenReturnAppointment(){
        // given
        // created in memory database for testing purposes
        Long l = 1l;

        // when
        Optional<Appointment> found = appointmentRepository.findById(1L);

        // then
        assertTrue("Appointment found", found.isPresent());
        assertEquals("Appointment contains right id", found.get().getId(), l);
    }


    @Test
    public void whenFindAllByOperationRoomId_thenReturnNotExisting(){
        // given
        // created in memory database for testing purposes

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllByOperationRoomId(1000L);

        // then
        assertFalse("Appointemnts found", found.isPresent());
    }


    @Test
    public void whenFindAllByOperationRoomId_thenReturnListOfAppointments(){
        // given
        // created in memory database for testing purposes

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllByOperationRoomId(1L);

        // then
        assertTrue("Appointemnts found", found.isPresent());
    }


    @Test
    public void whenFindAllByPatientId_thenReturnNotExisting(){
        // given
        // created in memory database for testing purposes

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllByPatientId(1000l);

        // then
        assertFalse("Nothing found", found.isPresent());
    }

    @Test
    public void whenFindByPatientId_thenReturnListOfAppointments(){
        // given
        // created in memory database for testing purposes

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllByPatientId(1L);

        // then
        assertTrue("Appointments found", found.isPresent());
    }


    @Test
    public void whenFindByServiceId_thenReturnNull(){
        // given
        // created in memory database for testing purposes
        Long service_id = 1000L;

        // when
        List<Appointment> found = appointmentRepository.findByServiceId(service_id);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindByServiceId_thenReturnListOfAppointments(){
        // given
        // created in memory database for testing purposes
        Long service_id = 1l;

        // when
        List<Appointment> found = appointmentRepository.findByServiceId(service_id);

        // then
        assertEquals(false, found.isEmpty());
    }


    @Test
    public void whenFindAllByClinicIdAndDateAfterAndPatientId_thenReturnNull(){
        // given
        // created in memory database for testing purposes
        Date date = new GregorianCalendar(2022, Calendar.FEBRUARY, 11).getTime();
        Long clinic_id = 1L;
        Long patient_id = 1L;

        // when
        List<Appointment> found = appointmentRepository.findAllByClinicIdAndDateAfterAndPatientId(clinic_id, date, patient_id);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindAllByClinicIdAndDateAfterAndPatientId_thenReturnListOfAppointments(){
        // given
        // created in memory database for testing purposes
        Date date = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();
        Long clinic_id = 1L;
        Long patient_id = 1L;

        // when
        List<Appointment> found = appointmentRepository.findAllByClinicIdAndDateAfterAndPatientId(clinic_id, date, patient_id);

        // then
        assertEquals(false, found.isEmpty());
    }


    @Test
    public void whenFindAllByNurse_thenReturnNull(){
        // given
        // created in memory database for testing purposes
        Long nurse_id = 1000L;

        // when
        List<Appointment> found = appointmentRepository.findAllByNurse(nurse_id);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindByNurse_thenReturnListOfAppointments(){
        // given
        // created in memory database for testing purposes
        Long nurse_id = 1000L;

        // when
        List<Appointment> found = appointmentRepository.findAllByNurse(nurse_id);

        // then
        assertEquals(false, found.size() != 0);
    }


    @Test
    public void whenFindAllAppointmentRequests_thenReturnNotExisting(){
        // given
        // created in memory database for testing purposes
        Long clinic_id = 1000L;

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllAppointmentRequests(clinic_id);

        // then
        assertFalse("Nothing found", found.isPresent());
    }


    @Test
    public void whenFindAllAppointmentRequests_thenReturnListOfAppointmentsForApproval(){
        // given
        // created in memory database for testing purposes
        Long clinic_id = 1L;

        // when
        Optional<List<Appointment>> found = appointmentRepository.findAllAppointmentRequests(clinic_id);

        // then
        assertTrue("Appointments waiting for approval", found.isPresent());
    }
}
