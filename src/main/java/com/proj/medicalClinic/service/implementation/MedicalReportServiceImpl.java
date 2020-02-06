package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.SimplifiedMedicalReportDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class MedicalReportServiceImpl implements MedicalReportService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private DrugsRepository drugsRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public void modifyMedicalReport(SimplifiedMedicalReportDTO simplifiedMedicalReportDTO) {
        try {
            MedicalReport medicalReport = medicalReportRepository.findAllById(simplifiedMedicalReportDTO.getMedicalReportID()).orElseThrow(() -> new NotExistsException("Medical report not found"));
            medicalReport.setExamDescription(simplifiedMedicalReportDTO.getExamDescription());

            if(simplifiedMedicalReportDTO.getSelectedDiagnosis() != null) {
                medicalReport.setDiagnosisRegistry(diagnosisRepository.findAllByIdIn(simplifiedMedicalReportDTO.getSelectedDiagnosis()));
            }

            if (simplifiedMedicalReportDTO.getSelectedDrugs() != null) {
                if (medicalReport.getPrescription() != null) {
                    medicalReport.getPrescription().setDrugsRegistry(drugsRepository.findAllByIdIn(simplifiedMedicalReportDTO.getSelectedDrugs()));
                } else {
                    Prescription prescription = new Prescription();
                    prescription.setApproved(false);
                    prescription.setDrugsRegistry(drugsRepository.findAllByIdIn(simplifiedMedicalReportDTO.getSelectedDrugs()));
                    prescription.setMedicalReport(medicalReport);
                    prescription.setNurse(medicalReport.getExamination().getNurse());
                    medicalReport.setPrescription(prescription);
                }
            }

            medicalReportRepository.save(medicalReport);
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void addMedicalReport(SimplifiedMedicalReportDTO simplifiedMedicalReportDTO) {
        try {
            MedicalReport medicalReport = new MedicalReport();
            Examination examination = examinationRepository.findById(simplifiedMedicalReportDTO.getExamID()).orElseThrow(() -> new NotExistsException("Examination not found"));
            Nurse nurse = nurseRepository.findByExaminations(examination);
            examination.setNurse(nurse);

            medicalReport.setExamination(examination);

            medicalReport.setExamDescription(simplifiedMedicalReportDTO.getExamDescription());

            if(simplifiedMedicalReportDTO.getSelectedDiagnosis() != null) {
                medicalReport.setDiagnosisRegistry(diagnosisRepository.findAllByIdIn(simplifiedMedicalReportDTO.getSelectedDiagnosis()));
            }

            if (simplifiedMedicalReportDTO.getSelectedDrugs() != null) {
                Prescription prescription = new Prescription();
                prescription.setApproved(false);
                prescription.setDrugsRegistry(drugsRepository.findAllByIdIn(simplifiedMedicalReportDTO.getSelectedDrugs()));
                //prescription.setMedicalReport(medicalReport);
                prescription.setNurse(medicalReport.getExamination().getNurse());
                prescriptionRepository.save(prescription);

                medicalReport.setPrescription(prescription);
            }

            medicalReport.setMedicalHistory(examination.getPatient().getMedicalHistory());
            System.out.println(medicalReport.getExamDescription());
            System.out.println(medicalReport.getMedicalHistory().getId());
            System.out.println(medicalReport.getPrescription().getId());
            System.out.println(medicalReport.getExamination().getId());

            String query = "INSERT INTO medical_report (exam_description, medical_history_id, prescription_id, examination_id) VALUES (?1, ?2, ?3, ?4)";
            Query queryEm = em.createNativeQuery(query)
                    .setParameter(1, medicalReport.getExamDescription())
                    .setParameter(2, medicalReport.getMedicalHistory().getId())
                    .setParameter(3, medicalReport.getPrescription().getId())
                    .setParameter(4, medicalReport.getExamination().getId());
            em.joinTransaction();
            queryEm.executeUpdate();

            List<Long> diagnosisIds = simplifiedMedicalReportDTO.getSelectedDiagnosis();
            MedicalReport medicalReportDiagnosis = medicalReportRepository.findByExamination(examination);
            for(Long diagnosisId: diagnosisIds) {
                String queryMR = "INSERT INTO medical_report_diagnosis (mreport_id, diagnosis_id) VALUES (?1, ?2)";
                Query queryMREm = em.createNativeQuery(queryMR)
                        .setParameter(1, medicalReportDiagnosis.getId())
                        .setParameter(2, diagnosisId);
                em.joinTransaction();
                queryMREm.executeUpdate();
            }
            //medicalReportRepository.saveMedicalReport(medicalReport.getExamDescription(), medicalReport.getMedicalHistory().getId(), medicalReport.getPrescription().getId(), medicalReport.getExamination().getId());
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }
}
