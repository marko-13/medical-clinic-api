package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class OperationRoomTest {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private OperationRoomRepository operationRoomRepository;

    @After
    public void cleanUp() {
        operationRoomRepository.deleteAll();
    }


    @Test
    public void whenFindAllByDeletedNot_thenReturnOperationRoom(){
        // given
        // created in memory database for testing purposes
        Optional<Clinic> c = clinicRepository.findById(1L);

        // when
        Optional<List<OperationRoom>> found = operationRoomRepository.findAllByClinic(c.get());

        // then
        assertTrue("Operation room does exist", found.isPresent());
    }
}
