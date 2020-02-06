package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private String type;
    private Date date;
    private double duration;
    private String fastExam;
    private String clinic;
    private String operationRoom;
    private String service;
    private List<DoctorDTO> doctors;
    private Long nurse;
    private String patient;
    private Long id;
    private MedicalReportDTO medicalReport;
    private boolean held;
    private boolean assigned;

    public AppointmentDTO(Appointment a){
        this.type = a.getClass().getAnnotation(DiscriminatorValue.class).value();
        this.doctors = new ArrayList<>();
        this.id = a.getId();

        if(this.type.equals("EX")){
            Examination examination = (Examination) a;
            this.date = examination.getDate();
            this.duration = examination.getDuration();
            this.held = examination.isHeld();

            if(examination.isFast()){
                this.fastExam = "Fast";
            }else{
                this.fastExam = "Regular";
            }

            this.clinic = examination.getClinic().getName();
            if(examination.getOperationRoom() == null){
                this.operationRoom = "";
            }else {
                this.operationRoom = examination.getOperationRoom().getName();
            }

            this.service = examination.getService().getType();

            if(examination.getNurse() == null){
                this.nurse = null;
            }else{
                this.nurse = examination.getNurse().getId();
            }

            if(examination.getPatient() == null){
                this.patient = "No patient";
            }else{
                this.patient = examination.getPatient().getName() + " " + examination.getPatient().getLastName();
            }

            if (examination.getMReport() != null) {
                this.medicalReport = new MedicalReportDTO(examination.getMReport());
            } else {
                this.medicalReport = null;
            }

            if (examination.getDoctors() != null) {
                this.doctors = examination.getDoctors().stream().map(
                        s -> new DoctorDTO(s)
                ).collect(Collectors.toList());
            } else {
                this.doctors = null;
            }
        }
        else{
            Operation operation = (Operation) a;

            this.date = operation.getDate();
            this.duration = operation.getDuration();

            this.fastExam = "Regular";

            this.clinic = operation.getClinic().getName();
            if (operation.getOperationRoom() == null){
                this.operationRoom = "";
            }else {
                this.operationRoom = operation.getOperationRoom().getName();
            }

            this.service = operation.getService().getType();

            if (operation.getDoctors() != null) {
                this.doctors = operation.getDoctors().stream().map(
                        s -> new DoctorDTO(s)
                ).collect(Collectors.toList());
            } else {
                this.doctors = null;
            }

            this.nurse = null;

            if(operation.getPatient() == null){
                this.patient = "No patient";
            }else{
                this.patient = operation.getPatient().getName() + " " +  operation.getPatient().getLastName();
            }

            this.medicalReport = null;
        }
    }

    public AppointmentDTO(Appointment a, boolean as){
        this.type = a.getClass().getAnnotation(DiscriminatorValue.class).value();
        this.doctors = new ArrayList<>();
        this.id = a.getId();
        this.assigned = as;

        if(this.type.equals("EX")){
            Examination examination = (Examination) a;
            this.date = examination.getDate();
            this.duration = examination.getDuration();
            this.held = examination.isHeld();

            if(examination.isFast()){
                this.fastExam = "Fast";
            }else{
                this.fastExam = "Regular";
            }

            this.clinic = examination.getClinic().getName();
            if(examination.getOperationRoom() == null){
                this.operationRoom = "";
            }else {
                this.operationRoom = examination.getOperationRoom().getName();
            }

            this.service = examination.getService().getType();

            if (examination.getDoctors() != null) {
                this.doctors = examination.getDoctors().stream().map(
                        s -> new DoctorDTO(s)
                ).collect(Collectors.toList());
            } else {
                this.doctors = null;
            }

            if(examination.getNurse() == null){
                this.nurse = null;
            }else{
                this.nurse = examination.getNurse().getId();
            }

            if(examination.getPatient() == null){
                this.patient = "No patient";
            }else{
                this.patient = examination.getPatient().getName() + " " + examination.getPatient().getLastName();
            }

            if (examination.getMReport() != null) {
                this.medicalReport = new MedicalReportDTO(examination.getMReport());
            } else {
                this.medicalReport = null;
            }
        }
        else{
            Operation operation = (Operation) a;

            this.date = operation.getDate();
            this.duration = operation.getDuration();

            this.fastExam = "Regular";

            this.clinic = operation.getClinic().getName();
            if (operation.getOperationRoom() == null){
                this.operationRoom = "";
            }else {
                this.operationRoom = operation.getOperationRoom().getName();
            }

            this.service = operation.getService().getType();

            if (operation.getDoctors() != null) {
                this.doctors = operation.getDoctors().stream().map(
                        s -> new DoctorDTO(s)
                ).collect(Collectors.toList());
            } else {
                this.doctors = null;
            }

            this.nurse = null;

            if(operation.getPatient() == null){
                this.patient = "No patient";
            }else{
                this.patient = operation.getPatient().getName() + " " +  operation.getPatient().getLastName();
            }

            this.medicalReport = null;
        }
    }

}
