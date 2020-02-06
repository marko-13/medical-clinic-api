package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.DrugsRegistryDTO;

import java.util.List;

public interface DrugsRegistryService {

    List<DrugsRegistryDTO> getAllDrugs(String email);

    DrugsRegistryDTO addDrug(DrugsRegistryDTO drugsRegistryDTO, String email);

}
