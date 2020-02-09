package com.proj.medicalClinic.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.google.common.collect.ImmutableMap;
import com.proj.medicalClinic.TranslateToJSON;
import com.proj.medicalClinic.dto.AppointmentRequestDTO;
import com.proj.medicalClinic.dto.NetxAppointmentRequestDTO;
import com.proj.medicalClinic.dto.StartExamDTO;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.RoleType;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import net.minidev.json.JSONArray;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DoctorControllerIntegrationTest {

    private String accessTokenDoctor;
    private String accessTokenAdminClinic;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/doctor";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

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


    @Test
    public void testGetAll_Success() throws Exception {
        mockMvc.perform(get(url + "/getAll")
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(4)));
    }

    @Test
    public void testGetCurrent_Success() throws Exception {
        mockMvc.perform(post(url + "/getCurrent/" + 7L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", isA(LinkedHashMap.class)))
                .andExpect(jsonPath("$.*").exists())
                .andExpect(jsonPath("$.*", notNullValue()))
                .andExpect(jsonPath("$.*", isA(JSONArray.class)));
    }

    @Test
    public void testGetCurrent_BadRequest() throws Exception {
        mockMvc.perform(post(url + "/getCurrent/" + 29L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void testSave_Success() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Marko");
        doctor.setAdress("test");
        doctor.setCity("test");
        doctor.setEmail("Marko@mailinator.com");
        doctor.setLastName("test");
        doctor.setMobile("06547568324");
        doctor.setPassword(passwordEncoder.encode("krokodil"));
        doctor.setState("Srbija");
        doctor.setUserRole(RoleType.DOCTOR);
        doctor.setReview(0);
        doctor.setReviewCount(0);
        doctor.setShift(1);
        doctor.setEnabled(true);
        doctor.setDeleted(false);
        doctor.setRejected(false);

        String translatedBody = TranslateToJSON.json(doctor);

        mockMvc.perform(post(url + "/save")
                .header("Authorization", accessTokenDoctor)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void testRemoveDoctor_Success() throws Exception {
        mockMvc.perform(post(url + "/remove/" + 9L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", isA(LinkedHashMap.class)))
                .andExpect(jsonPath("$.*").exists())
                .andExpect(jsonPath("$.*", notNullValue()))
                .andExpect(jsonPath("$.*", isA(JSONArray.class)))
                .andExpect(jsonPath("$.*").value(hasItem("Dragance")));
    }

    //NotExist -> Not Found
    @Test
    public void testRemoveDoctor_NotFound() throws Exception {
        mockMvc.perform(post(url + "/remove/" + 58L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testRemoveDoctor_Forbidden() throws Exception {
        mockMvc.perform(post(url + "/remove/" + 4L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

    //NotExist -> BadRequest
    ///reviewed/
    @Test
    public void testReceiveReview_Success() throws Exception {
        mockMvc.perform(post(url + "/reviewed/" + 4L + "/" + 4)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Success"));
    }

    @Test
    public void testReceiveReview_BadRequest() throws Exception {
        mockMvc.perform(post(url + "/reviewed/" + 98L + "/" + 4)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    //NotExist -> NotFound
    // /getAllAvailable
    // getAllAvailable
    @Test
    public void testGetAllAvailable_Success() throws Exception {
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setStart(1608850800000L);
        appointmentRequestDTO.setRoomId(3L);
        appointmentRequestDTO.setAppId(7L);

        String translatedBody = TranslateToJSON.json(appointmentRequestDTO);

        mockMvc.perform(post(url + "/getAllAvailable")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(2)));
    }

    @Test
    public void testGetAllAvailable_NotFound() throws Exception {
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setStart(1608850800000L);
        appointmentRequestDTO.setRoomId(3L);
        appointmentRequestDTO.setAppId(98L);

        String translatedBody = TranslateToJSON.json(appointmentRequestDTO);

        mockMvc.perform(post(url + "/getAllAvailable")
                .header("Authorization", accessTokenAdminClinic)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isNotFound());
    }


    //getMedicalHistoryStartExamination
    ///start-exam/{patientId}
    //NotValid -> Forbidden
    //Not Exists -> BadRequest
    //Exception -> NotFound
    @Test
    public void testGetAllAvailableStartExam_Success() throws Exception {
        mockMvc.perform(post(url + "/start-exam/" + 1L)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.isAllowed").value(false));
    }

    @Test
    public void testGetAllAvailableStartExam_BadRequest() throws Exception {
        mockMvc.perform(post(url + "/start-exam/" + 58)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Patient not found"));
    }



    @Test
    public void testGetAllAvailableStartExam_Forbidden() throws Exception {
        mockMvc.perform(post(url + "/start-exam/" + 1L)
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }


    //finish-exam/{patientId}
    //NotValid -> Forbidden
    //Not Exists -> BadRequest
    //Exception -> NotFound
    //Success: "Successfully finished the examination"
    @Test
    public void testSaveMedicalHistoryFinishExamination_Success() throws Exception {
        StartExamDTO startExamDTO = new StartExamDTO();
        startExamDTO.setWeight(65.0);
        startExamDTO.setHeight(185);
        startExamDTO.setDioptre(1);
        startExamDTO.setAllergies("cija semenke, kolokvijumi");
        startExamDTO.setMedicalHistoryId(1L);
        startExamDTO.setExamID(9L);

        String translatedBody = TranslateToJSON.json(startExamDTO);

        mockMvc.perform(post(url + "/finish-exam/" + 1L)
                .contentType(contentType)
                .content(translatedBody)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Successfully finished the examination"));
    }

    @Test
    public void testSaveMedicalHistoryFinishExamination_BadRequest() throws Exception {
        StartExamDTO startExamDTO = new StartExamDTO();
        startExamDTO.setWeight(65.0);
        startExamDTO.setHeight(185);
        startExamDTO.setDioptre(1);
        startExamDTO.setAllergies("cija semenke, kolokvijumi");
        startExamDTO.setMedicalHistoryId(1L);
        startExamDTO.setExamID(9L);

        String translatedBody = TranslateToJSON.json(startExamDTO);

        mockMvc.perform(post(url + "/finish-exam/" + 98L)
                .contentType(contentType)
                .content(translatedBody)
                .header("Authorization", accessTokenDoctor))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("Patient not found"));
    }

    ///getAllAvailableForExam/{clinc_id}/{selected_date}/{service_id}
    //NotExistException -> NotFound
    //Return doctorsDTOS
    @Test
    public void testGetAllFromClinicAndIsNotDeleted_Success() throws Exception {
        mockMvc.perform(get(url + "/getAllFromClinicAndNotDeleted")
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").value(hasSize(2)));
    }
}
