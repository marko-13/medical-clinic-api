package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.PrescriptionDTO;
import com.proj.medicalClinic.model.Nurse;
import com.proj.medicalClinic.model.Prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionService {

    PrescriptionDTO approvePrescription (String email, Long id);

    List<PrescriptionDTO> getApprovedPrescriptions (String email);

    List<PrescriptionDTO> getNotApprovedPrescriptions (String email);

}
