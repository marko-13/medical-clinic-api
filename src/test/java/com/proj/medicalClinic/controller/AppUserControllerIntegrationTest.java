package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import net.minidev.json.JSONArray;
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

import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppUserControllerIntegrationTest {

    private String accessTokenPatient;
    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/user";

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
    public void loginPatient() {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Miljana@mailinator.com", "miljana"), UserTokenState.class);
        accessTokenPatient = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testGetUserProfile_Success() throws Exception {
        mockMvc.perform(get(url + "/profile")
                .header("Authorization", accessTokenPatient))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", isA(LinkedHashMap.class)))
                .andExpect(jsonPath("$.*").exists())
                .andExpect(jsonPath("$.*", notNullValue()))
                .andExpect(jsonPath("$.*", isA(JSONArray.class)))
                .andExpect(jsonPath("$.*").value(hasItem("Miljana")));
    }

}
