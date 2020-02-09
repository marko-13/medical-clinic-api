package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private  OperationRepository operationRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @After
    public void cleanUp() {
        doctorRepository.deleteAll();
    }


    @Test
    public void whenFindById_thenReturnNotExisting(){
        // given
        // created in memory database for testing purposes
        Long doctor_id = 1000L;

        // when
        Optional<Doctor> found = doctorRepository.findById(doctor_id);

        // then
        assertFalse("Doctor does not exist", found.isPresent());
    }


    @Test
    public void whenFindById_thenReturnDoctor(){
        // given
        // created in memory database for testing purposes
        Long doctor_id = 4L;

        // when
        Optional<Doctor> found = doctorRepository.findById(doctor_id);

        // then
        assertTrue("Doctor found", found.isPresent());
    }


    @Test
    public void whenFindByDeletedNot_thenReturnListOfDoctorsThatAreNotDeleted(){
        // given
        // created in memory database for testing purposes
        boolean deleted = true;

        // when
        List<Doctor> found = doctorRepository.findAllByDeletedNot(deleted);

        // then
        assertEquals(false, found.isEmpty());
    }


    @Test
    public void whenFindByDeletedNot_thenReturnListOfDoctorsThatAreDeleted(){
        // given
        // created in memory database for testing purposes
        boolean deleted = false;

        // when
        List<Doctor> found = doctorRepository.findAllByDeletedNot(deleted);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindByServices_thenReturnListOfDoctors(){
        // given
        // created in memory database for testing purposes
        Optional<Service> s = serviceRepository.findById(1L);

        // when
        List<Doctor> found = doctorRepository.findAllByServices(s.get());

        // then
        assertEquals(false, found.isEmpty());
    }


    @Test
    public void whenFindByServices_thenReturnNull(){
        // given
        // created in memory database for testing purposes
        Service s = new Service();
        s.setPrice(1);
        s.setType("Neki dugacak pregled");
        s.setDeleted(false);
        s.setId(1000L);
        s.setDoctors(new ArrayList<Doctor>());
        s.setAppointments(new ArrayList<Appointment>());
        s.setClinics(new ArrayList<Clinic>());

        // when
        List<Doctor> found = doctorRepository.findAllByServices(s);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindByClinic_thenReturnListOfDoctors(){
        // given
        // created in memory database for testing purposes
        Optional<Clinic> c = clinicRepository.findById(1L);

        // when
        List<Doctor> found = doctorRepository.findAllByClinic(c.get());

        // then
        assertEquals(false, found.isEmpty());
    }


    @Test
    public void whenFindByClinic_thenReturnNull(){
        // given
        // created in memory database for testing purposes
        Clinic c = new Clinic();
        c.setPatients(new ArrayList<Patient>());
        c.setAddress("Losa adresa");
        c.setDescription("Wow");
        c.setId(1000L);
        c.setName("Kliniketina");
        c.setReview(100);
        c.setReviewCount(20);
        c.setClinicalCenter(new ClinicalCenter());
        c.setAdminsClinic(new ArrayList<AdminClinic>());
        c.setDoctors(new ArrayList<Doctor>());
        c.setAppointments(new ArrayList<Appointment>());
        c.setNurses(new ArrayList<Nurse>());
        c.setServices(new ArrayList<Service>());

        // when
        List<Doctor> found = doctorRepository.findAllByClinic(c);

        // then
        assertEquals(true, found.isEmpty());
    }


    @Test
    public void whenFindAllByOperation_thenReturnListOfDoctors(){
        // given
        // created in memory database for testing purposes
        Optional<Operation> o = operationRepository.findById(4L);

        // when
        List<Doctor> found = doctorRepository.findAllByOperations(o.get());

        // then
        assertEquals(false, found.isEmpty());
    }
}
