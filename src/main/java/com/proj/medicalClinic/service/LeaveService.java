package com.proj.medicalClinic.service;

import com.proj.medicalClinic.dto.LeaveDTO;

import java.util.List;

public interface LeaveService {

    List<LeaveDTO> getAll();

    void approveLeave(Long id, String email);
    void denyLeave(Long id, String email, String message);

}
