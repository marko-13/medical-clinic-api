package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AppointmentDTO;
import com.proj.medicalClinic.dto.ClinicDTO;
import com.proj.medicalClinic.dto.DiagnosisRegistryDTO;
import com.proj.medicalClinic.dto.MedicalHistoryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public MedicalHistoryDTO getMedicalHistory(String email) {

        Patient patient = (Patient) userDetailsService.loadUserByUsername(email);


        MedicalHistory medicalHistory = medicalHistoryRepository.findByPatient(patient)
                .orElseThrow(NotExistsException::new);

        MedicalHistoryDTO medicalHistoryDTO = new MedicalHistoryDTO(medicalHistory);

        return medicalHistoryDTO;

    }

    @Override
    public MedicalHistoryDTO getMedicalHistoryByPatientId(Long patientId, String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email).orElseThrow(NotExistsException::new);
            List<Appointment> appointments = appointmentRepository.findAllByPatientId(patientId).orElse(null);
            boolean assigned = false;
            List<AppointmentDTO> editedAppointments = new ArrayList<>();

            if (appointments != null) {
                if (user instanceof Doctor) {
                    Doctor dr = (Doctor) user;
                    for (Appointment ap : appointments) {

                        if (ap instanceof Examination) {
                            Examination ex = (Examination) ap;
                            List<Doctor> doctors = doctorRepository.findAllByExaminations(ex);

                            if (doctors.contains(dr)) {
                                assigned = true;
                                editedAppointments.add(new AppointmentDTO(ap, true));
                            } else {
                                editedAppointments.add(new AppointmentDTO(ap, false));
                            }

                        } else if (ap instanceof Operation) {
                            Operation op = (Operation) ap;
                            List<Doctor> doctors = doctorRepository.findAllByOperations(op);

                            if (doctors.contains(dr)) {
                                assigned = true;
                                editedAppointments.add(new AppointmentDTO(ap, true));
                            } else {
                                editedAppointments.add(new AppointmentDTO(ap, false));
                            }
                        }
                    }
                } else if (user instanceof Nurse) {
                    Nurse nr = (Nurse) user;

                    for (Appointment ap : appointments) {

                        if (ap instanceof Examination) {
                            Examination ex = (Examination) ap;
                            if (ex.getNurse() != null) {
                                if (ex.getNurse().getId() == nr.getId()) {
                                    assigned = true;
                                    editedAppointments.add(new AppointmentDTO(ap, true));
                                } else {
                                    editedAppointments.add(new AppointmentDTO(ap, false));
                                }
                            } else {
                                editedAppointments.add(new AppointmentDTO(ap, false));
                            }
                        }
                    }
                }

                if (assigned) {
                    MedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patientId).orElseThrow(NotExistsException::new);
                    MedicalHistoryDTO medicalHistoryDTO = new MedicalHistoryDTO(medicalHistory, editedAppointments);

                    return medicalHistoryDTO;
                } else {
                    throw new NotExistsException("Not authorized to see this patient's medical history.");
                }
            } else {
                throw new NotExistsException("Not authorized to see this patient's medical history.");
            }
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }
}
