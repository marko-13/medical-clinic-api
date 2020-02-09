package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.TranslateToJSON;
import com.proj.medicalClinic.dto.AppointmentRequestDTO;
import com.proj.medicalClinic.dto.DoctorDTO;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import com.proj.medicalClinic.service.DoctorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DoctorControllerUnitTests {

    private String accessTokenDoctor;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/doctor";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private DoctorService doctorServiceMock;


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

    @Test
    public void testGetAllAvailableDoctorsSuccess() throws Exception {

        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setAppId(4l);
        appointmentRequestDTO.setRoomId(2l);
        appointmentRequestDTO.setStart(1583276400000l);

        String translatedBody = TranslateToJSON.json(appointmentRequestDTO);

        DoctorDTO doctorDTO1 = new DoctorDTO();
        DoctorDTO doctorDTO2 = new DoctorDTO();


        List<DoctorDTO> doctorDTOS = new ArrayList<>();
        doctorDTOS.add(doctorDTO1);
        doctorDTOS.add(doctorDTO2);

        Mockito.when(doctorServiceMock.getAllAvailableForDate(appointmentRequestDTO)).thenReturn(doctorDTOS);

        mockMvc.perform(post(url + "/getAllAvailable")
                .header("Authorization", accessTokenDoctor)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(0)));

    }
}
