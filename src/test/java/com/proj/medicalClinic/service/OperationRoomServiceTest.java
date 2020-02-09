package com.proj.medicalClinic.service;


import com.proj.medicalClinic.dto.OperationRoomDTO;
import com.proj.medicalClinic.repository.OperationRoomRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OperationRoomServiceTest{

    @Autowired
    private OperationRoomService operationRoomService;

    @Mock
    private OperationRoomRepository operationRoomRepository;


    @Test
    public void whenGetAll_thenThrowNotExistsException(){
        // given
        // created in memory database for testing purposes

        // when
        List<OperationRoomDTO> ret = operationRoomService.getAll();
        //System.out.println(ret.size());

        // then
        assertEquals(3, ret.size());
    }

    @Test
    public void whenGetAllAvailable_thenReturnAllOperationRooms(){
        // given
        // created in memory database for testing purposes

        // when
        List<OperationRoomDTO> found = operationRoomService.getAllAvailable(new Date().getTime());
        //  Mockito.when(appointmentRepositoryMock.findAllByPatientId(1L)).thenReturn(Optional.of(list));
        // assertEquals(1, appointmentService.getAllByPatient(1L).size());

        // then
        assertEquals(3, found.size());
    }


    @Test
    public void whenGetAllAvailable_thenReturnAvailableOperationRooms(){
        // given
        // created in memory database for testing purposes
        String string = "2019-01-12";
        DateFormat format = new SimpleDateFormat("YYYY-mm-dd");
        Date date = new Date();
        try {
             date = format.parse(string);
        }catch (ParseException t){

        }
        // when
        List<OperationRoomDTO> found = operationRoomService.getAllAvailable(date.getTime());

        // then
        assertEquals(3, found.size());
    }


    @Test( expected = NullPointerException.class)
    public void whenGetAllFromClinic_thenReturn(){
        // given
        // created in memory database for testing purposes

        // when
        List<OperationRoomDTO> found = operationRoomService.getAllFromClinic();

        // then
    }

}
