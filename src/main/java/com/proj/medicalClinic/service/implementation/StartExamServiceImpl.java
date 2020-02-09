package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.StartExamDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.StartExamService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
public class StartExamServiceImpl implements StartExamService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public StartExamDTO getMedicalHistoryStartExamination(String email, Long patientId) {
        try {
            System.out.println("Usao 1");
            StartExamDTO startExamDTO = new StartExamDTO();
            startExamDTO.setIsAllowed(false);
            AppUser userDoctor = appUserRepository.findByEmail(email).orElseThrow(() -> new NotExistsException("Doctor not found"));
            System.out.println("Usao 2");
            AppUser userPatient = appUserRepository.findById(patientId).orElseThrow(() -> new NotExistsException("Patient not found"));
            System.out.println("Usao 3");
            Doctor doctor = new Doctor();
            Patient patient = new Patient();

            if(userDoctor instanceof Doctor) {
                System.out.println("Usao 4");
                doctor = (Doctor) userDoctor;
            } else {
                throw new NotValidParamsException("Only doctors can start the examination.");
            }

            if(userPatient instanceof Patient) {
                System.out.println("Usao 5");
                patient = (Patient) userPatient;
            } else {
                throw new NotValidParamsException("Only doctors can start the examination.");
            }

            System.out.println("Usao 6");
            List<Appointment> appointments = appointmentRepository.findAllByPatientId(patientId).orElse(null);
            System.out.println("Usao 7");
            MedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patientId).orElse(null);
            System.out.println("Usao 8");
            //Proveri da li je held, proveri da li je taj doktor, setuj held na kraju kad budes slao zahtev
            //Mora operation room da bude dodeljen isto
            if (appointments != null ) {
                for(Appointment ap: appointments) {
                    if (ap instanceof Examination) {
                        Examination ex = (Examination) ap;
                        Doctor dr = doctorRepository.findByExaminations(ex);
                        if(DateUtils.isSameDay(ap.getDate(), new Date()) && dr.getId() == doctor.getId() && ap.getOperationRoom() != null && !ex.isHeld()) {
                            startExamDTO.setIsAllowed(true);
                            startExamDTO.setFirstName(patient.getName());
                            startExamDTO.setLastName(patient.getLastName());
                            startExamDTO.setMedicalHistoryId(medicalHistory.getId());
                            startExamDTO.setHeight(medicalHistory.getHeight());
                            startExamDTO.setWeight(medicalHistory.getWeight());
                            startExamDTO.setDioptre(medicalHistory.getDioptre());
                            startExamDTO.setAllergies(medicalHistory.getAllergy());
                            startExamDTO.setExamID(ap.getId());
                            break;
                        }
                    }
                }

                return startExamDTO;
            } else {
                return startExamDTO;
            }


        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void saveMedicalHistoryFinishExamination(String email, StartExamDTO startExamDTO, Long patientId) {
        try {
            AppUser userDoctor = appUserRepository.findByEmail(email).orElseThrow(() -> new NotExistsException("Doctor not found"));
            Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new NotExistsException("Patient not found"));
            Doctor doctor = new Doctor();

            if(userDoctor instanceof Doctor) {
                doctor = (Doctor) userDoctor;
            } else {
                throw new NotValidParamsException("Only doctors can start the examination.");
            }

            //MedicalHistory medicalHistory = medicalHistoryRepository.findById(startExamDTO.getMedicalHistoryId()).orElseThrow(() -> new NotExistsException("Not able to find medical history"));
            System.out.println("OVDE ISPISUJE NESTO");
            System.out.println(startExamDTO.getWeight());
            System.out.println(startExamDTO.getHeight());
            System.out.println(startExamDTO.getDioptre());
            System.out.println(startExamDTO.getAllergies());
            System.out.println(startExamDTO.getMedicalHistoryId());
            String queryMH = "update medical_history set weight=?1, height=?2, dioptre=?3, allergy=?4 where patient_id=?5";
            Query queryEMMH = em.createNativeQuery(queryMH)
                    .setParameter(1, startExamDTO.getWeight())
                    .setParameter(2, startExamDTO.getHeight())
                    .setParameter(3, startExamDTO.getDioptre())
                    .setParameter(4, startExamDTO.getAllergies())
                    .setParameter(5, startExamDTO.getMedicalHistoryId());
            em.joinTransaction();
            queryEMMH.executeUpdate();

            /*medicalHistory.setWeight(startExamDTO.getWeight());
            medicalHistory.setHeight(startExamDTO.getHeight());
            medicalHistory.setDioptre(startExamDTO.getDioptre());
            medicalHistory.setAllergy(startExamDTO.getAllergies());
            medicalHistoryRepository.save(medicalHistory);*/
            System.out.println("Sacuvao medical history");

            Appointment appointment = appointmentRepository.findById(startExamDTO.getExamID()).orElseThrow(() -> new NotExistsException("Not able to find examination"));
            if(appointment instanceof Examination) {
                String query = "update appointment set held=?1 where id=?2";
                Query queryEM = em.createNativeQuery(query)
                        .setParameter(1, true)
                        .setParameter(2, appointment.getId());
                em.joinTransaction();
                queryEM.executeUpdate();
            } else {
                throw new NotValidParamsException("Appointment is not an examination");
            }

        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }
}
