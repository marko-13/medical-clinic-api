package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.DoctorDTO;
import com.proj.medicalClinic.dto.LeaveDTO;
import com.proj.medicalClinic.dto.NurseDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.model.Leave;
import com.proj.medicalClinic.model.Nurse;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.LeaveRepository;
import com.proj.medicalClinic.service.EmailService;
import com.proj.medicalClinic.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public List<LeaveDTO> getAll() {

        List<Leave> leaves = leaveRepository.findAllUnapproved();
        if(leaves == null){
            throw new NotExistsException("There are no leaves");
        }

        List<LeaveDTO> leaveDTOS = new ArrayList<>();

        for(Leave l : leaves){
            if(l.getNurse() != null){
                AppUser appUser = appUserRepository.findById(l.getNurse().getId()).orElseThrow(NotExistsException::new);
                NurseDTO nurseDTO = new NurseDTO((Nurse) appUser);
                leaveDTOS.add(new LeaveDTO(l, nurseDTO));
            }else{
                AppUser appUser = appUserRepository.findById(l.getDoctor().getId()).orElseThrow(NotExistsException::new);
                DoctorDTO doctorDTO = new DoctorDTO((Doctor) appUser);
                leaveDTOS.add(new LeaveDTO(l, doctorDTO));
            }
        }

        return leaveDTOS;
    }

    @Override
    public void approveLeave(Long id, String email){
        Leave leave = leaveRepository.findById(id).orElseThrow(NotExistsException::new);
        leave.setApproved(true);
        leave.setActive(false);


        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(NotExistsException::new);


        try{
            this.emailService.sendNotificaitionAsync(appUser, " your leave of absence has been approved<br></br>", "Leave of absence");
        }catch (Exception e) {
            e.printStackTrace();
        }

        leaveRepository.save(leave);

    }

    @Override
    public void denyLeave(Long id, String email, String message){
        Leave leave = leaveRepository.findById(id).orElseThrow(NotExistsException::new);
        leave.setApproved(false);
        leave.setActive(false);


        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(NotExistsException::new);

        try{
            this.emailService.sendNotificaitionAsync(appUser, message, "Leave of absence");
        }catch (Exception e) {
            e.printStackTrace();
        }

        leaveRepository.save(leave);

    }
}
