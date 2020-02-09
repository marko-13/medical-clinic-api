package com.proj.medicalClinic;

import com.proj.medicalClinic.controller.*;
import com.proj.medicalClinic.repository.AppointmentRepositoryTest;
import com.proj.medicalClinic.repository.DoctorRepositoryTest;
import com.proj.medicalClinic.repository.LeaveRepositoryTest;
import com.proj.medicalClinic.repository.OperationRoomTest;

import com.proj.medicalClinic.service.AppUserServiceTest;
import com.proj.medicalClinic.service.AppointmentServiceIntegrationTest;
import com.proj.medicalClinic.service.AppointmentServiceTest;
import com.proj.medicalClinic.service.OperationRoomServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({AppointmentRepositoryTest.class, DoctorRepositoryTest.class, LeaveRepositoryTest.class, OperationRoomTest.class,
        AppointmentServiceTest.class, AppUserServiceTest.class, OperationRoomServiceTest.class, AppointmentServiceIntegrationTest.class, AppointmentControllerIntegrationTest.class, DoctorControllerIntegrationTest.class, NurseControllerIntegrationTest.class, AppUserControllerIntegrationTest.class,
        AppointmentControllerUnitTest.class, AppUserControllerUnitTest.class, DoctorControllerUnitTests.class})
public class TestSuite {
}

