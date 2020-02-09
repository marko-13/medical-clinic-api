package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.TranslateToJSON;
import com.proj.medicalClinic.dto.AppointmentDTO;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import com.proj.medicalClinic.service.AppointmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppointmentControllerUnitTest {

    private String accessTokenAdmin;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/appointment";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private AppointmentService appointmentServiceMock;

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
                new JwtAuthenticationRequest("Anastasija@mailinator.com", "anastasija"), UserTokenState.class);
        accessTokenAdmin = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testGetAllAppointmentRequestsSuccess() throws Exception {

        AppointmentDTO appointmentDTO1 = new AppointmentDTO();
        AppointmentDTO appointmentDTO2 = new AppointmentDTO();
        AppointmentDTO appointmentDTO3 = new AppointmentDTO();

        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        appointmentDTOS.add(appointmentDTO1);
        appointmentDTOS.add(appointmentDTO2);
        appointmentDTOS.add(appointmentDTO3);

        Mockito.when(appointmentServiceMock.getAllAppointmentRequests()).thenReturn(appointmentDTOS);

        mockMvc.perform(get(url + "/getAllAppointmentRequests")
                .header("Authorization", accessTokenAdmin)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(3)));
    }

    @Test
    public void testAddRoomToAppointmentSuccess() throws Exception {

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAssigned(true);

        Long appointmentId = 3l;
        Long roomId = 2l;

        Mockito.when(appointmentServiceMock.addRoom(appointmentId, roomId)).thenReturn(appointmentDTO);

        mockMvc.perform(post(url + "/addRoomToAppointment/" + appointmentId + "/" + roomId)
                .header("Authorization", accessTokenAdmin)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.[*]").value(hasItem(true)));


    }

    @Test
    public void testaddOperationRoomToAppointment() throws Exception {

        long appointmentId = 5l;
        long roomId = 2l;
        List<Long> doctorIds = new ArrayList<>();

        doctorIds.add(3l);
        doctorIds.add(4l);

        String translatedBody = TranslateToJSON.json(doctorIds);
        System.out.println(translatedBody);

        mockMvc.perform(post(url + "/addOperationRoomToAppointment/" + appointmentId + "/" + roomId)
                .header("Authorization", accessTokenAdmin)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added room to the operation"));

    }

    @Test
    public void testaddChangedOperationRoomToAppointment() throws Exception {

        long appointmentId = 5l;
        long roomId = 2l;
        long start = 1583276400000l;

        List<Long> doctorIds = new ArrayList<>();

        doctorIds.add(3l);
        doctorIds.add(4l);

        String translatedBody = TranslateToJSON.json(doctorIds);

        mockMvc.perform(post(url + "/addChangedOperationRoomToAppointment/" + appointmentId + "/" + roomId + "/" + start)
                .header("Authorization", accessTokenAdmin)
                .contentType(contentType)
                .content(translatedBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added room to the operation"));

    }



}
