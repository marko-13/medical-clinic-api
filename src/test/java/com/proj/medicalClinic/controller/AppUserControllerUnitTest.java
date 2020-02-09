package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.UserTokenState;
import com.proj.medicalClinic.security.authentication.JwtAuthenticationRequest;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.implementation.CustomUserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppUserControllerUnitTest {

    private String accessToken;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    public static final String url = "/user";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Mock
    private CustomUserDetailsServiceImpl appUserServiceMock;

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
    public void login() {
        ResponseEntity<UserTokenState> responseEntity = testRestTemplate.postForEntity("/users/login",
                new JwtAuthenticationRequest("Anastasija@mailinator.com", "anastasija"), UserTokenState.class);
        accessToken = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testProfileSuccess() throws Exception {

        AppUser appUser = new AppUser();

        Mockito.when(appUserServiceMock.loadUserByUsername("Anastasija@mailinator.com")).thenReturn(appUser);

        mockMvc.perform(get(url + "/profile")
                .header("Authorization", accessToken)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.[*]").value(hasItem("Anastasija")));
    }

}
