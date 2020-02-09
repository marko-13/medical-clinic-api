package com.proj.medicalClinic.service;

import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.repository.AppUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppUserServiceTest {

    @Autowired
    private AppUserService appUserService;

    @MockBean
    private AppUserRepository appUserRepositoryMock;

    @Test(expected = NotExistsException.class)
    public void shouldThrowNotExistsExceptionWhenAppUserDoesNotExist(){
        when(appUserRepositoryMock.findByEmail("123@gmail.com")).thenThrow(NotExistsException.class);

        appUserRepositoryMock.findByEmail("123@gmail.com");
    }
}
