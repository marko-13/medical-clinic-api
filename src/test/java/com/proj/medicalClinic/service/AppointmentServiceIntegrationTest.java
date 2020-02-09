package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AppointmentDTO;
import com.proj.medicalClinic.dto.AppointmentHistoryDTO;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.repository.AppointmentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations="classpath:application-test.properties")
@ActiveProfiles("test")
public class AppointmentServiceIntegrationTest {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    @Transactional
    public void getAllByOperationRoom() {
        List<AppointmentDTO> appointments = appointmentService.getAllByOperationRoom(1L);
        assertEquals(appointments.size(), 2);
    }

    @Test
    public void getAllByPatient() {
        List<AppointmentHistoryDTO> appointmentsHistory = appointmentService.getAllByPatient(1L);
        assertEquals(appointmentsHistory.size(), 3);
    }

}
