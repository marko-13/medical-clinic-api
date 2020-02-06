package com.proj.medicalClinic;

import com.proj.medicalClinic.controller.AppUserControllerTest;
import com.proj.medicalClinic.repository.AppUserRepositoryTest;
import com.proj.medicalClinic.service.AppUserServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@RunWith(Suite.class)
@Suite.SuiteClasses({AppUserRepositoryTest.class, AppUserServiceTest.class})
public class TestSuite {
}

