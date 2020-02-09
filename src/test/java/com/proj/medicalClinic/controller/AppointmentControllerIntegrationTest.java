package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.TranslateToJSON;
import com.proj.medicalClinic.dto.AppointmentRequestDTO;
import com.proj.medicalClinic.dto.ChangeDoctorRequestDTO;
import com.proj.medicalClinic.dto.ClinicReviewRequestDTO;
import com.proj.medicalClinic.dto.NetxAppointmentRequestDTO;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;


import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppointmentControllerIntegrationTest {

    private String accessTokenDoctor;
    private String accessTokenAdminClinic;
    private String accessTokenPatient;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/appointment";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void loginDoctor() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Vladan@mailinator.com", "vladan"), UserTokenState.class);
        accessTokenDoctor = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Before
    public void loginAdminClinic() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Anastasija@mailinator.com", "anastasija"), UserTokenState.class);
        accessTokenAdminClinic = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Before
    public void loginPatient() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Miljana@mailinator.com", "miljana"), UserTokenState.class);
        accessTokenPatient = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testGetAllByOperationRoom_Success() throws Exception {
        mockMvc.perform(get(url + "/getAllByOperationRoom/" + 1L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(2)));
    }

    @Test
    public void testGetAllByPatient_Success() throws Exception {
        mockMvc.perform(get(url + "/getAllByPatient/" + 1L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(3)));
    }

    @Test
    public void testGetAllByPatientNoParams_Success() throws Exception {
        mockMvc.perform(get(url + "/getAllAppointmentRequests")
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddRoom_NotAllowed() throws Exception {
        mockMvc.perform(post(url + "/addRoomToAppointment/" + 7L + "/" + 8L)
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddRoom_Success() throws Exception {
        mockMvc.perform(post(url + "/addRoomToAppointment/" + 7L + "/" + 3L)
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.operationRoom").value("Ordinacija"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAddOperationRoomToAppointment_Success() throws Exception {
        List<Long> doctorsIds = new ArrayList<Long>() {
            {
                add(4L);
            }
        };

        String translatedBody = TranslateToJSON.json(doctorsIds);

        mockMvc.perform(post(url + "/addOperationRoomToAppointment/" + 8L + "/" + 1L)
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added room to the operation"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAddChangedOperationRoomToAppointment_Success() throws Exception {
        List<Long> doctorsIds = new ArrayList<Long>() {
            {
                add(4L);
            }
        };

        String translatedBody = TranslateToJSON.json(doctorsIds);

        mockMvc.perform(post(url + "/addChangedOperationRoomToAppointment/" + 8L + "/" + 1L + "/" + 1575988200000L)
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added room to the operation"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testChangeDateAndAddRoomToApointment_Success() throws Exception {
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setAppId(5L);
        appointmentRequestDTO.setRoomId(1L);
        appointmentRequestDTO.setStart(1575280800000L);

        String translatedBody = TranslateToJSON.json(appointmentRequestDTO);

        mockMvc.perform(post(url + "/changeDateAndAddRoomToApointment")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testChangeDateAndAddRoomToApointment_NotFound() throws Exception {
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setAppId(259L);
        appointmentRequestDTO.setRoomId(1L);
        appointmentRequestDTO.setStart(1575280800000L);

        String translatedBody = TranslateToJSON.json(appointmentRequestDTO);

        mockMvc.perform(post(url + "/changeDateAndAddRoomToApointment")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testChangeDoctorAndAddRoomToAppointment_Success() throws Exception {
        ChangeDoctorRequestDTO changeDoctorRequestDTO = new ChangeDoctorRequestDTO();
        changeDoctorRequestDTO.setAppId(7L);
        changeDoctorRequestDTO.setDoctorId(4L);
        changeDoctorRequestDTO.setRoomId(3L);

        String translatedBody = TranslateToJSON.json(changeDoctorRequestDTO);

        mockMvc.perform(post(url + "/changeDoctorAndAddRoomToAppointment")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetAllHeldAndBetweenDates_Success() throws  Exception {
        ClinicReviewRequestDTO clinicReviewRequestDTO = new ClinicReviewRequestDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateInStringStart = "05-12-2019 15:00:00";
        String dateInStringEnd = "13-12-2019 15:00:00";
        Date dateStart = sdf.parse(dateInStringStart);
        Date dateEnd = sdf.parse(dateInStringEnd);
        clinicReviewRequestDTO.setStartDate(dateStart);
        clinicReviewRequestDTO.setEndDate(dateEnd);

        String translatedBody = TranslateToJSON.json(clinicReviewRequestDTO);

        mockMvc.perform(post(url + "/getAllHeldAndBetweenDates")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").value(hasSize(1)));

    }

    @Test
    public void testReserveExaminationAsPatient_Success() throws Exception {
        mockMvc.perform(post(url + "/reserve/" + 1607853600000L + "/" + 0L + "/" + 0L + "/" + 3L + "/" + 1L)
                .header("Authorization", accessTokenPatient))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Reserved"));
    }

    @Test
    public void testReserveExaminationAsPatientBody_Success() throws Exception {
        NetxAppointmentRequestDTO netxAppointmentRequestDTO = new NetxAppointmentRequestDTO();
        netxAppointmentRequestDTO.setAppointmentType("Operation");
        netxAppointmentRequestDTO.setLastAppointmentId(3L);
        netxAppointmentRequestDTO.setStartDate(1568970000000L);

        String translatedBody = TranslateToJSON.json(netxAppointmentRequestDTO);

        mockMvc.perform(post(url + "/addAnotherForPatient")
                .header("Authorization", accessTokenDoctor)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Appointment added successfully"));

    }

    @Test
    public void testFindAllAvailableFastExams_Success() throws Exception {
        mockMvc.perform(get(url + "/getAllFastForClinic/" + 1L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").value(hasSize(1)));
    }


}