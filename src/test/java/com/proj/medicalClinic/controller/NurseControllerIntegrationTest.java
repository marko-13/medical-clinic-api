package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NurseControllerIntegrationTest {

    private String accessTokenNurse;
    private String accessTokenAdminClinic;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/nurse";

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
    public void loginNurse() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Sergej@mailinator.com", "sergej"), UserTokenState.class);
        accessTokenNurse = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Before
    public void loginAdminClinic() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Anastasija@mailinator.com", "anastasija"), UserTokenState.class);
        accessTokenAdminClinic = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testGetWorkSchedule_Success() throws Exception {
        mockMvc.perform(post(url + "/get-work-schedule")
                .header("Authorization", accessTokenNurse))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").value(hasSize(2)));
    }

    @Test
    public void testGetWorkSchedule_Forbidden() throws Exception {
        mockMvc.perform(post(url + "/get-work-schedule")
                .header("Authorization", accessTokenAdminClinic))
                .andExpect(status().isForbidden());
    }
}
