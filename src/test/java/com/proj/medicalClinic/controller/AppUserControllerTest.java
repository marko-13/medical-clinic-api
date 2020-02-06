package com.proj.medicalClinic.controller;

import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.service.AppUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppUserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private AppUserService appUserServiceMock;

    //NE RADI VRACA UNAUTHORIZED
    @Test
    public void updateEndpointShouldReturnNotFoundWhenNotExistsExceptionIsThrown(){
        when(appUserServiceMock.findByEmail("123@gmail.com")).thenThrow(NotExistsException.class);

        ResponseEntity<?> responseEntity = testRestTemplate.postForEntity("/user/update", null, null);

        assertEquals("Http status is NOT FOUND", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
