package com.proj.medicalClinic.cron;

import com.proj.medicalClinic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoScheduler {

    @Autowired
    private AppointmentService appointmentService;

    //za svake tri sekunde cron = "0/3 * * ? * *"
    //svaki dan u 12 cron = "0 0 0 * * ?"
    @Scheduled(cron = "0 0 0 * * ?")
    public void addRoomsWithCrone() {
       appointmentService.cronAddRooms();
    }

}
