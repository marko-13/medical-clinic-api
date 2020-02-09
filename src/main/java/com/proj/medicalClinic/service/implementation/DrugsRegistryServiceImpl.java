package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.DrugsRegistryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.DrugsRepository;
import com.proj.medicalClinic.service.DrugsRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DrugsRegistryServiceImpl implements DrugsRegistryService {

    @Autowired
    private DrugsRepository drugsRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public List<DrugsRegistryDTO> getAllDrugs(String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter) && !(user instanceof Doctor) && !(user instanceof Nurse)) {
                throw new NotValidParamsException("Only admin of the clinical center can see this data");
            }

            List<DrugsRegistry> drugsRegistry = drugsRepository.findAll();

            List<DrugsRegistryDTO> drugsRegistryDTO = drugsRegistry.stream().map(
                    s -> new DrugsRegistryDTO(s)
            ).collect(Collectors.toList());

            return drugsRegistryDTO;
        } catch (NotExistsException e) {
            throw e;
        }
    }

    @Override
    public DrugsRegistryDTO addDrug(DrugsRegistryDTO drugsRegistryDTO, String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter)) {
                throw new NotValidParamsException("Only admin of the clinical center add this data");
            }

            if (drugsRegistryDTO == null) {
                throw new NotExistsException("Params for drugsRegistryDTO are not valid");
            }

            Optional<DrugsRegistry> unique = this.drugsRepository.findByDrugName(drugsRegistryDTO.getDrugName());

            if (unique.isPresent()) {
                throw new NotValidParamsException("Drug with that name already exists");
            }

            DrugsRegistry newDrugsRegistry = new DrugsRegistry();
            newDrugsRegistry.setDrugName(drugsRegistryDTO.getDrugName());

            this.drugsRepository.save(newDrugsRegistry);

            DrugsRegistryDTO newDrugsRegistryDTO = new DrugsRegistryDTO(newDrugsRegistry);

            return newDrugsRegistryDTO;
        } catch (NotValidParamsException | NotExistsException e) {
            throw e;
        }
    }
}
