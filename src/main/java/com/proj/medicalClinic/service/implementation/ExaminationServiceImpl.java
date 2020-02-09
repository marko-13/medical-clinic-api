package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.ExaminationRequestDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public void saveNewExamination(ExaminationRequestDTO exRequest) {

        Date startDate = new Date(exRequest.getExamStart());
        Doctor doctor = doctorRepository.findById(exRequest.getDoctorId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(exRequest.getOperationRoomId()).orElseThrow(NotExistsException::new);
        com.proj.medicalClinic.model.Service service = serviceRepository.findById(exRequest.getServiceId()).orElseThrow(NotExistsException::new);
        Clinic clinic = clinicRepository.findById(operationRoom.getClinic().getId()).orElseThrow(NotExistsException::new);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor);

        long newExStart = startDate.getTime();
        long newExEnd = (long) (newExStart + exRequest.getDuration() * 60000);

        List<AppUser> allNurses = appUserRepository.findByUserRole(RoleType.NURSE).orElse(null);
        Nurse nurse = null;

        for (AppUser ap: allNurses) {
            Nurse nr = (Nurse) ap;
            if(nr.getClinic().getId() == doctor.getClinic().getId()) {
                boolean assignedNurse = true;
                List<Appointment> appointmentsNurse = appointmentRepository.findAllByNurse(nr.getId());
                List<Leave> leaveNurse = leaveRepository.findAllByNurse(nr);

                for (Appointment app : appointmentsNurse) {
                    Examination ex = (Examination) app;
                    long exStart = ex.getDate().getTime();

                    if (newExStart <= exStart && exStart <= newExEnd) {
                        System.out.println("Usao 1 new");
                        assignedNurse = false;
                        break;
                    }
                }

                if (assignedNurse) {
                    for (Leave leave : leaveNurse) {
                        long leaveStart = leave.getDateStart().getTime();
                        long leaveEnd = leave.getDateEnd().getTime();

                        if (leaveStart <= newExStart && leaveEnd >= newExStart) {
                            System.out.println("Usao 2 new");
                            assignedNurse = false;
                            break;
                        }
                    }

                    if (assignedNurse) {
                        System.out.println("Usao 3 new " + nr.getEmail());
                        nurse = nr;
                        break;
                    }
                }
            }
        }

        if (nurse == null) {
            System.out.println("Usao 4 new");
            nurse = (Nurse) allNurses.get(allNurses.size() - 1);
        }

        Examination newExamination = new Examination();
        newExamination.setDate(startDate);
        newExamination.setDoctors(doctors);
        newExamination.setOperationRoom(operationRoom);
        newExamination.setService(service);
        newExamination.setDuration(exRequest.getDuration());
        newExamination.setFast(true);
        newExamination.setClinic(clinic);
        newExamination.setNurse(nurse);
        newExamination.setConfirmed(2);

        //sacuvaj novi examination
        examinationRepository.save(newExamination);

        //dodaj ga doktoru
        doctor.getExaminations().add(newExamination);
        doctorRepository.save(doctor);
    }
}
