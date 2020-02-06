package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Leave;
import com.proj.medicalClinic.model.Nurse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LeaveRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LeaveRepository leaveRepository;

    @After
    public void cleanUp() {
        leaveRepository.deleteAll();
    }

    private static final long LEAVE_ID = 1;

    private static final Date DATE_START = new Date(System.currentTimeMillis() + 150000000);

    private static final Date DATE_END = new Date(System.currentTimeMillis() + 250000000);

    private static final boolean ACTIVE = false;

    private static final boolean APPROVED = false;

    private static final Doctor DOCTOR = new Doctor();

    private static final Nurse NURSE = new Nurse();



    @Test
    public void shouldReturnEmptyOptionalWhenFindingNonExistingLeaveById() {
        Optional<Leave> foundFlight = leaveRepository.findById(LEAVE_ID);

        assertFalse("Leave is not present.", foundFlight.isPresent());
    }

    @Test
    public void shouldReturnLeaveWhenFindingExistingLeaveById() {
        Leave leave = new Leave(LEAVE_ID, DATE_START, DATE_END, APPROVED, ACTIVE, DOCTOR, NURSE);
        testEntityManager.persist(leave);
        testEntityManager.flush();

        Optional<Leave> foundLeave = leaveRepository.findById(LEAVE_ID);
        assertTrue("Flight is present.", foundLeave.isPresent());
        assertCorrectLeaveIsReturned(foundLeave.get());
    }

    private void assertCorrectLeaveIsReturned(Leave leave) {
        assertEquals("Leave contains correct ID.", Optional.ofNullable(leave.getId()), LEAVE_ID);
        assertEquals("Leave contains correct start date.", leave.getDateStart(), DATE_START);
        assertEquals("Leave contains correct end date.", leave.getDateEnd(), DATE_END);
        assertEquals("Leave contains correct approved leave.", leave.isApproved(), APPROVED);
        assertEquals("Leave contains correct active leave.", leave.isActive(), ACTIVE);
        assertEquals("Leave contains correct doctor.", leave.getDoctor(), DOCTOR);
        assertEquals("Leave contains correct nurse.", leave.getNurse(), NURSE);
    }

}
