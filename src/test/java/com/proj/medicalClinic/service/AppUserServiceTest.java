package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.AppointmentHistoryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.repository.AppUserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppUserServiceTest {

    @Autowired
    private AppUserService appUserService;

    @Mock
    private AppUserRepository appUserRepository;


    @Test(expected = NotExistsException.class)
    public void shouldThrowNotExistsExceptionWhenAppUserDoesNotExist(){

        appUserService.findByEmail("nepostoji@mailinator.com");
    }


    @Test
    public void shouldReturnAppUser(){
        // given
        // created in memory database for testing purposes
        AppUser ap = new AppUser();
        ap.setEmail("Sergej@mailinator.com");
        ap.setId(1L);

        // when
        Mockito.when(appUserRepository.findByEmail("Sergej@mailinator.com")).thenReturn(Optional.of(ap));

        // then
        AppUser found = appUserService.findByEmail("Sergej@mailinator.com");

        assertEquals("Sergej@mailinator.com", found.getEmail());

    }

}
