package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AppointmentDTO;
import com.proj.medicalClinic.dto.AppointmentHistoryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AppointmentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Test(expected = NotExistsException.class)
    public void whenGetAllByPatientId_thenThrowNotExistsException(){
        // given
        // created in memory database for testing purposes

        // when

        // then
        appointmentService.getAllByPatient(1000L);
    }

    @Test
    public void whenGetAllByPatientId_thenReturnListOfAppointemtns(){
        // given
        // created in memory database for testing purposes
        Patient p = new Patient();
        p.setId(2L);

        Appointment ap = new Appointment();
        ap.setPatient(p);
        ap.setId(2L);

        List<Appointment> found = new ArrayList<>();
        found.add(ap);

        Mockito.when(appointmentRepository.findAllByPatientId(2L)).thenReturn(Optional.of(found));

        // when
        List<AppointmentHistoryDTO> found1 = appointmentService.getAllByPatient(2L);

        // then
        assertEquals(1, found1.size());
    }


    @Test( expected = NullPointerException.class)
    public void whenGetAllByOperationRoomId_thenThrowNullPointerException(){
        // given
        // created in memory database for testing purposes

        // when
        List<AppointmentDTO> found = appointmentService.getAllAppointmentRequests();

        // then
        assertEquals(1, found.size());
    }


    @Test( expected = NotExistsException.class)
    public void whenGetAllAppointmentsByMedicalStaff_thenThrowNotExistsException(){
        // given
        // created in memory database for testing purposes

        // when
        List<AppointmentHistoryDTO> found = appointmentService.getAllAppointmentsByMedicalStaffMember("nepostoji@mailinator.com");

        // then
    }


    @Test
    public void whenGetAllAppointmentsByMedicalStaff_thenReturnListOfAppointmentsNurse(){
        // given
        // created in memory database for testing purposes
        Examination ex = new Examination();
        ex.setId(1L);

        Doctor d = new Doctor();
        d.setId(1L);

        List<Doctor> docs = new ArrayList<>();
        docs.add(d);

        List<Examination> exams = new ArrayList<>();
        exams.add(ex);

        ex.setDoctors(docs);
        d.setExaminations(exams);

        List<Appointment> found1 = new ArrayList<>();
        Mockito.when(appointmentRepository.findAllByNurse(2L)).thenReturn(found1);

        // when
        List<AppointmentHistoryDTO> found = appointmentService.getAllAppointmentsByMedicalStaffMember("Sergej@mailinator.com");

        // then
        assertEquals(2, found.size());
    }


    @Test
    public void whenGetAllDayBeforeAndDayAfter_thenReturnNull(){
        // given
        // created in memory database for testing purposes

        // when
        List<Appointment> found = appointmentService.getAllDayBeforeAndDayAfter(new Date(), new Date());

        // then
        assertEquals(null, found);
    }


    @Test
    public void whenGetAllDayBeforeAndDayAfter_thenReturnListOFAppointments(){
        // given
        // created in memory database for testing purposes
        String string = "2022-01-12";
        DateFormat format = new SimpleDateFormat("YYYY-mm-dd");
        Date date1 = new Date();
        try {
            date1 = format.parse(string);
        }catch (ParseException t){

        }
        string = "2010-01-12";
        Date date2 = new Date();
        try {
            date2 = format.parse(string);
        }catch (ParseException t){

        }

        List<Appointment> found1 = new ArrayList<>();
        Mockito.when(appointmentRepository.findAllByDateBetweenAndOperationRoomIsNotNull(date1, date2)).thenReturn(found1);

        // when
        List<Appointment> found = appointmentService.getAllDayBeforeAndDayAfter(date2, date1);

        // then
        assertEquals(6, found.size());
    }


    @Test( expected = NotExistsException.class)
    public void whenAddRoom_thenThrowNotExistsException(){
        // given
        // created in memory database for testing purposes

        // when
        AppointmentDTO a = appointmentService.addRoom(1000L, 1000L);

        // then
    }


//    @Test
//    public void whenAddRoom_thenReturnAppointemnt(){
//        // given
//        // created in memory database for testing purposes
//        // kako logovati korisnika
//        // SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(null, null, Arrays.asList(new SimpleGrantedAuthority("PATIENT"))));
//
//
//        // when
//        AppointmentDTO a = appointmentService.addRoom(1L, 1L);
//
//        // then
//        assertEquals(Optional.of(1L), a.getId());
//    }
}
