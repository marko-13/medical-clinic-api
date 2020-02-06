package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.DiagnosisRegistryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.DiagnosisRepository;
import com.proj.medicalClinic.service.DiagnosisRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiagnosisRegistryServiceImpl implements DiagnosisRegistryService {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public List<DiagnosisRegistryDTO> getAllDiagnosis(String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter) && !(user instanceof Doctor) && !(user instanceof Nurse)) {
                throw new NotValidParamsException("Only admin of the clinical center can see this data");
            }

            List<DiagnosisRegistry> diagnosisRegistry = diagnosisRepository.findAll();

            List<DiagnosisRegistryDTO> diagnosisRegistryDTO = diagnosisRegistry.stream().map(
                    s -> new DiagnosisRegistryDTO(s)
            ).collect(Collectors.toList());

            return diagnosisRegistryDTO;
        } catch (NotExistsException e) {
            throw e;
        }
    }

    @Override
    public DiagnosisRegistryDTO addDiagnosis(DiagnosisRegistryDTO diagnosisRegistryDTO, String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter)) {
                throw new NotValidParamsException("Only admin of the clinical center add this data");
            }

            if (diagnosisRegistryDTO == null) {
                throw new NotExistsException("Params for diagnosisRegistryDTO are not valid");
            }


            Optional<DiagnosisRegistry> unique = this.diagnosisRepository.findByDiagnosisName(diagnosisRegistryDTO.getDiagnosisName());

            if (unique.isPresent()) {
                throw new NotValidParamsException("Diagnosis with that name already exists");
            }

            DiagnosisRegistry newDiagnosisRegistry = new DiagnosisRegistry();
            newDiagnosisRegistry.setDiagnosisName(diagnosisRegistryDTO.getDiagnosisName());

            this.diagnosisRepository.save(newDiagnosisRegistry);

            DiagnosisRegistryDTO newDiagnosisRegistryDTO = new DiagnosisRegistryDTO(newDiagnosisRegistry);

            return newDiagnosisRegistryDTO;
        } catch (NotValidParamsException | NotExistsException e) {
            throw e;
        }
    }
}
