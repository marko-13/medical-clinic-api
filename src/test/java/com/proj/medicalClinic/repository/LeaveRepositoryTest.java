package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Leave;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class LeaveRepositoryTest {

    @Autowired
    private LeaveRepository leaveRepository;

    @After
    public void cleanUp() {
        leaveRepository.deleteAll();
    }


    @Test
    public void shouldReturnEmptyOptionalWhenFindingNonExistingLeaveById() {
        // given
        // created in memory database for testing purposes

        // when
        Optional<Leave> found = leaveRepository.findById(1000L);

        // then
        assertFalse("Leave is not present.", found.isPresent());
    }

    @Test
    public void shouldReturnLeaveWhenFindingExistingLeaveById() {
        // given
        // created in memory database for testing purposes

        // when
        Optional<Leave> foundLeave = leaveRepository.findById(1L);

        // then
        assertTrue("Leave is present.", foundLeave.isPresent());
    }

}
