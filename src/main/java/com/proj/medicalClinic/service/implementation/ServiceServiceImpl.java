package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.ServiceDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.AdminClinic;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.model.Clinic;
import com.proj.medicalClinic.model.Doctor;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public List<ServiceDTO> getAllFromClinic() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Long clinicId = adminClinic.getClinic().getId();

        List<com.proj.medicalClinic.model.Service> services = serviceRepository.findAllByClinic(clinicId);

        if(services == null){
            throw new NotExistsException("Ne postoji ni jedan tip pregleda u toj klinici.");
        }else{
            List<ServiceDTO> serviceDTOS = new ArrayList<>();
            for(com.proj.medicalClinic.model.Service s : services){
                if(!s.isDeleted()){
                    serviceDTOS.add(new ServiceDTO(s));
                }
            }
            return serviceDTOS;
        }
    }

    @Override
    public ServiceDTO save(ServiceDTO serviceRequest) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Long clinicId = adminClinic.getClinic().getId();

        Clinic clinic = clinicRepository.findById(clinicId).orElseThrow(NotExistsException::new);
        List<Clinic> clinics = new ArrayList<>();
        clinics.add(clinic);


        com.proj.medicalClinic.model.Service service = new com.proj.medicalClinic.model.Service();
        service.setType(serviceRequest.getServiceType());
        service.setPrice(serviceRequest.getPrice());
        clinic.getServices().add(service);

        //REDOSLED JE BITAN!!
        serviceRepository.save(service);
        clinicRepository.save(clinic);

        return new ServiceDTO(service);
    }

    @Override
    public ServiceDTO remove(Long serviceId) {
        List<Appointment> appointment = appointmentRepository.findByServiceId(serviceId);
        if (appointment.isEmpty()){
            com.proj.medicalClinic.model.Service service = serviceRepository.findById(serviceId).orElseThrow(NotExistsException::new);
            System.out.println(service.getType());
            service.setDeleted(true);
            serviceRepository.save(service);
            return new ServiceDTO(service);
        }else {
            throw new ResourceConflictException(serviceId, "Ovaj tip pregleda je zakazan!");
        }
    }

    @Override
    public ServiceDTO edit(ServiceDTO serviceReq) {
        List<Appointment> appointment = appointmentRepository.findByServiceId(serviceReq.getId());
        if (appointment.isEmpty()){
            com.proj.medicalClinic.model.Service service = serviceRepository.findById(serviceReq.getId()).orElseThrow(NotExistsException::new);
            service.setType(serviceReq.getServiceType());
            service.setPrice(serviceReq.getPrice());
            serviceRepository.save(service);
            return new ServiceDTO(service);
        }else {
            throw new ResourceConflictException(serviceReq.getId(), "Ovaj tip pregleda je zakazan!");
        }
    }

    @Override
    public List<ServiceDTO> getAllFromDoctor(long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(NotExistsException::new);
        List<com.proj.medicalClinic.model.Service> services = serviceRepository.findAllByDoctors(doctor);

        if(services == null || services.isEmpty()){
            throw new NotExistsException();
        }

        List<ServiceDTO> serviceDTOS = new ArrayList<>();
        for(com.proj.medicalClinic.model.Service s : services){
            serviceDTOS.add(new ServiceDTO(s));
        }

        return serviceDTOS;

    }

    // return list of services that are not deleted
    @Override
    public List<ServiceDTO> getAllNotDeleted() {
        List<com.proj.medicalClinic.model.Service> services = serviceRepository.findAllWhereDeletedFalse();
        List<ServiceDTO> ret = new ArrayList<>();

        for(com.proj.medicalClinic.model.Service s : services){
            ret.add(new ServiceDTO(s));
        }

        return ret;
    }

    @Override
    public List<ServiceDTO> getAllHeldAndFromOneYearAndFromClinic() {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Long clinicId = adminClinic.getClinic().getId();

        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.YEAR, -1);
        Date yearBefore = c.getTime();

        List<com.proj.medicalClinic.model.Service> services = serviceRepository.findAllHeldAndNotDeletedAndFromBeforeOneYearAndFromClinic(yearBefore,currentDate, clinicId);
        if(services == null || services.isEmpty()){
            throw new NotExistsException();
        }

        List<ServiceDTO> serviceDTOS = new ArrayList<>();
        for(com.proj.medicalClinic.model.Service s : services){
            serviceDTOS.add(new ServiceDTO(s));
        }

        return serviceDTOS;

    }

}
