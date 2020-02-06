package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.ExaminationRequestDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.Clinic;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Examination;
import com.proj.medicalClinic.model.OperationRoom;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExaminationServiceImpl implements ExaminationService {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OperationRoomRepository operationRoomRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Override
    public void saveNewExamination(ExaminationRequestDTO exRequest) {

        Date startDate = new Date(exRequest.getExamStart());
        Doctor doctor = doctorRepository.findById(exRequest.getDoctorId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(exRequest.getOperationRoomId()).orElseThrow(NotExistsException::new);
        com.proj.medicalClinic.model.Service service = serviceRepository.findById(exRequest.getServiceId()).orElseThrow(NotExistsException::new);
        Clinic clinic = clinicRepository.findById(operationRoom.getClinic().getId()).orElseThrow(NotExistsException::new);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor);

        Examination newExamination = new Examination();
        newExamination.setDate(startDate);
        newExamination.setDoctors(doctors);
        newExamination.setOperationRoom(operationRoom);
        newExamination.setService(service);
        newExamination.setDuration(exRequest.getDuration());
        newExamination.setFast(true);
        newExamination.setClinic(clinic);

        //sacuvaj novi examination
        examinationRepository.save(newExamination);

        //dodaj ga doktoru
        doctor.getExaminations().add(newExamination);
        doctorRepository.save(doctor);
    }
}
