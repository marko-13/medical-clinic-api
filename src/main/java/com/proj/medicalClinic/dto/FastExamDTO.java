package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastExamDTO {

    private Long id;
    private Date date;
    private String operationRoom;
    private String doctor;
    private String service;
    private double price;
    private double discount;

    public FastExamDTO(Appointment a, Doctor d){
        this.id = a.getId();
        this.date = a.getDate();
        this.operationRoom = a.getOperationRoom().getName();
        this.doctor = d.getName() + " " + d.getLastName();
        this.service = a.getService().getType();
        this.price = a.getService().getPrice();

        Random rand = new Random();
        // nextInt as provided by Random is exclusive of the top value so you need to add 1
        double randomNum = rand.nextInt((10 - 1) + 1) + 1;
        this.discount = randomNum;

        System.out.println(a.getOperationRoom().getName());
    }

}
