package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.*;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.AdminClinic;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.AppointmentService;
import com.proj.medicalClinic.service.EmailService;
import com.proj.medicalClinic.service.OperationRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.text.SimpleDateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private OperationRoomRepository operationRoomRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OperationRoomService operationRoomService;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private AdminClinicRepository adminClinicRepository;

    @PersistenceContext
    EntityManager em;


    @Override
    public List<AppointmentDTO> getAllByOperationRoom(Long id){
        System.out.println("Prosao ovaj 1");
        List<Appointment> appointments = appointmentRepository.findAllByOperationRoomId(id)
                .orElse(null);
        System.out.println("Prosao ovaj 2");
        if (appointments == null) {
            System.out.println("Prosao ovaj 3");
        }
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();

        if(appointments != null){
            for(Appointment a : appointments){
                System.out.println("Prosao ovaj 4");
                appointmentDTOS.add(new AppointmentDTO(a));
            }
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentHistoryDTO> getAllByPatient(Long id) {
        List<Appointment> appointments = appointmentRepository.findAllByPatientId(id)
                .orElseThrow(NotExistsException::new);

        List<AppointmentHistoryDTO> appointmentDTOS = new ArrayList<>();

        for(Appointment a : appointments){
            if(a instanceof Examination) {
                if (((Examination) a).getConfirmed() == 2) {
                    appointmentDTOS.add(new AppointmentHistoryDTO(a));
                }
            } else {
                appointmentDTOS.add(new AppointmentHistoryDTO(a));
            }
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentDTO> getAllAppointmentRequests() {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Long clinicId = adminClinic.getClinic().getId();

        List<Appointment> appointments = appointmentRepository.findAllAppointmentRequests(clinicId).orElseThrow(NotExistsException::new);
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        for (Appointment a : appointments) {
            System.out.println("Id appointmenta " + a.getId());
            appointmentDTOS.add(new AppointmentDTO(a));
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentHistoryDTO> getAllAppointmentsByMedicalStaffMember(String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof Doctor || user instanceof Nurse)) {
                throw new NotValidParamsException("Only medical staff members can see this data");
            }

            if (user instanceof Nurse) {
                System.out.println(email);
                System.out.println(user.getId());
                List<Appointment> appointments = appointmentRepository.findAllByNurse(user.getId());
                for(Appointment ap: appointments) {
                    System.out.println(ap.getId());
                }

                List<AppointmentHistoryDTO> appointmentHistoryDTO = appointments.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                return appointmentHistoryDTO;
            } else {
                List<Examination> examinations = examinationRepository.findAllByDoctorsContainingAndConfirmed((Doctor) user, 2);

                List<AppointmentHistoryDTO> appointmentHistoryDTO = examinations.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                List<Operation> operations = operationRepository.findAllByDoctorsContaining((Doctor) user);

                List<AppointmentHistoryDTO> appointmentHistoryDTO1 = operations.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                appointmentHistoryDTO.addAll(appointmentHistoryDTO1);
                return appointmentHistoryDTO;
            }
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    public List<Appointment> getAllDayBeforeAndDayAfter(Date before, Date after) {

        List<Appointment> appointments = appointmentRepository.findAllByDateBetweenAndOperationRoomIsNotNull(before, after);
        if(appointments.isEmpty()){
            return null;
        }
        return appointments;
    }

    @Override
    public AppointmentDTO addRoom(Long appointmentId, Long roomId) {

        System.out.println("Prosao0 " + appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(NotExistsException::new);
        System.out.println("Prosao " + appointment.getId());
        OperationRoom operationRoom = operationRoomRepository.findById(roomId).orElseThrow(NotExistsException::new);
        System.out.println("Prosao1 " + operationRoom.getId());
        appointment.setOperationRoom(operationRoom);

        //POSALJI MAIL PACIJENTU
        Patient patient = appointment.getPatient();


        Long app = appointmentId;
        try {
            this.emailService.sendNotificaitionAsync(patient, "<a href=\"http://localhost:3000/confirm_exam?"+"val=2&app="+app+"\">Confirm</a> <br></br> <a href=\"http://localhost:3000/confirm_exam?"+"val=3&app=" + app + "\""+">Deny</a>", "Appointment confirmation");
        }catch (Exception e){
            e.printStackTrace();
        }

        //POSALJI MAIL DOKTORU
        if(appointment instanceof Examination){

            Examination ex = (Examination) appointment;
            List<Doctor> doctors = ex.getDoctors();

            if(doctors != null || !doctors.isEmpty()){

                Doctor doctor = doctors.get(0);

                try {
                    this.emailService.sendNotificaitionAsync(doctor,
                            "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() ,
                            "Appointment confirmation");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        appointmentRepository.save(appointment);
        return new AppointmentDTO(appointment);
    }

    @Override
    @Transactional
    public void addOperationRoom(Long appointmentId, Long roomId, List<Long> doctorsId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotExistsException("Appointment not found"));
            OperationRoom operationRoom = operationRoomRepository.findById(roomId).orElseThrow(() -> new NotExistsException("Room not found"));

            appointment.setOperationRoom(operationRoom);

            //POSALJI MAIL PACIJENTU
            Patient patient = appointment.getPatient();
            try {
                this.emailService.sendNotificaitionAsync(patient, "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br>Room: " + appointment.getOperationRoom().getName() + " " +appointment.getOperationRoom().getNumber(), "Appointment confirmation");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //POSALJI MAIL DOKTORU
            if (appointment instanceof Operation) {

                Operation op = (Operation) appointment;
                List<Doctor> doctors = new ArrayList<>();

                for (Long doctorId : doctorsId) {
                    Doctor dr = doctorRepository.findById(doctorId).orElse(null);
                    String queryMR = "INSERT INTO doctors_operations (doctor_id, operation_id) VALUES (?1, ?2)";
                    Query queryMREm = em.createNativeQuery(queryMR)
                            .setParameter(1, dr.getId())
                            .setParameter(2, appointmentId);
                    em.joinTransaction();
                    queryMREm.executeUpdate();
                    doctors.add(dr);
                }

                ((Operation) appointment).setDoctors(doctors);

                if (doctors != null || !doctors.isEmpty()) {

                    for (Doctor dr : doctors) {
                        try {
                            this.emailService.sendNotificaitionAsync(dr,
                                    "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() + "<br></br>Room: " + appointment.getOperationRoom().getName() + " " +appointment.getOperationRoom().getNumber(),
                                    "Appointment confirmation");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new NotValidParamsException("There isn't any doctor");
                }
            } else {
                throw new NotValidParamsException("This is an examination, not an operation.");
            }

            appointmentRepository.save(appointment);
            System.out.println("Sacuvao appointment");
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void addChangedOperationRoom(Long appointmentId, Long roomId, List<Long> doctorsId, Long start) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotExistsException("Appointment not found"));
            OperationRoom operationRoom = operationRoomRepository.findById(roomId).orElseThrow(() -> new NotExistsException("Room not found"));
            Date selectedDate = new Date(start);

            appointment.setOperationRoom(operationRoom);
            appointment.setDate(selectedDate);

            //POSALJI MAIL PACIJENTU
            Patient patient = appointment.getPatient();
            try {
                this.emailService.sendNotificaitionAsync(patient, "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br>Room: " + appointment.getOperationRoom().getName() + " " +appointment.getOperationRoom().getNumber(), "Appointment confirmation");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //POSALJI MAIL DOKTORU
            if (appointment instanceof Operation) {

                Operation op = (Operation) appointment;
                List<Doctor> doctors = new ArrayList<>();

                for (Long doctorId : doctorsId) {
                    Doctor dr = doctorRepository.findById(doctorId).orElse(null);
                    String queryMR = "INSERT INTO doctors_operations (doctor_id, operation_id) VALUES (?1, ?2)";
                    Query queryMREm = em.createNativeQuery(queryMR)
                            .setParameter(1, dr.getId())
                            .setParameter(2, appointmentId);
                    em.joinTransaction();
                    queryMREm.executeUpdate();
                    doctors.add(dr);
                }

                if (doctors != null || !doctors.isEmpty()) {

                    for (Doctor dr : doctors) {
                        try {
                            this.emailService.sendNotificaitionAsync(dr,
                                    "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() + "<br></br>Room: " + appointment.getOperationRoom().getName() + " " +appointment.getOperationRoom().getNumber(),
                                    "Appointment confirmation");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new NotValidParamsException("There isn't any doctor");
                }
            } else {
                throw new NotValidParamsException("This is an examination, not an operation.");
            }

            appointmentRepository.save(appointment);
            System.out.println("Sacuvao appointment");
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    public AppointmentDTO changeDateAndAddRoom(AppointmentRequestDTO appointmentRequestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(appointmentRequestDTO.getRoomId()).orElseThrow(NotExistsException::new);

        long selectedDateMilisecds = appointmentRequestDTO.getStart();
        Date selectedDate = new Date(selectedDateMilisecds);


        if (appointment instanceof  Examination){
            Examination examination = (Examination) appointment;
            List<Doctor> doctors = doctorRepository.findAllByExaminations(examination);

            if(doctors == null){
                throw new NotExistsException();
            }

            Doctor currentDoctor = doctors.get(0);

            for(Examination ex : currentDoctor.getExaminations()){
                long exStart = ex.getDate().getTime();
                long exEnd = (long) (exStart + ex.getDuration() * 60000);

                System.out.println("---------------");
                System.out.println(ex.getId());
                System.out.println(exStart + " PRE");
                System.out.println(selectedDateMilisecds + " SELEKTOVAN");
                System.out.println(exEnd + " POSLE");
                System.out.println("---------------");

                if(selectedDateMilisecds >= exStart && selectedDateMilisecds <= exEnd){
                    System.out.println("USAO VAMO");
                    throw new ResourceConflictException(ex.getId(), "Doktor " + currentDoctor.getName() + " vec ima zakazan pregled u ovo vreme");
                }
            }

            appointment.setDate(selectedDate);
            appointment.setOperationRoom(operationRoom);

            //SALJI MAIL PATIENTU
            Patient patient = appointment.getPatient();


            Long app = appointmentRequestDTO.getAppId();
            try {
                this.emailService.sendNotificaitionAsync(patient, "<a href=\"http://localhost:3000/confirm_exam?"+"val=2&app="+app+"\">Confirm</a> <br></br> <a href=\"http://localhost:3000/confirm_exam?"+"val=3&app=" + app + "\""+">Deny</a>", "Appointment confirmation");
            }catch (Exception e){
                e.printStackTrace();
            }

            //SALJI MAIL DOKTORU
            try {
                this.emailService.sendNotificaitionAsync(currentDoctor,
                        "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() ,
                        "Appointment confirmation");
            }catch (Exception e){
                e.printStackTrace();
            }


            appointmentRepository.save(appointment);
            return new AppointmentDTO(appointment);
        }
        return null;
    }

    @Transactional
    public void cronAddRooms(){

        //Appointment a = appointmentRepository.findById(7l);
        //appointmentRepository.save(a);
        //OVO NE RADI

        List<Appointment> unnaprovedRequests = appointmentRepository.findAllByOperationRoomIsNull();
        if(unnaprovedRequests.isEmpty() || unnaprovedRequests == null){
            System.out.println("NEMA ZAHTEVA");
            return;
        }

        System.out.println(unnaprovedRequests.size());

        List<OperationRoom> operationRooms = operationRoomRepository.findAllByDeletedNot(true);
        if(operationRooms.isEmpty() || operationRooms == null){
            System.out.println("NEMA SOBA");
            return;
        }

        for(Appointment unnaprovedApp : unnaprovedRequests){

            if(unnaprovedApp instanceof Examination){
                Patient patient = unnaprovedApp.getPatient();
                Examination ex = (Examination) unnaprovedApp;
                //UZMI SVE SLOBODNE SOBE ZA TAJ DATUM
                List<OperationRoomDTO> availableRooms = operationRoomService.getAllAvailable(ex.getDate().getTime());
                if(availableRooms.isEmpty() || availableRooms == null){
                    continue;
                }

                //PROVERI DA JE APPOINTMENT I SOBA IZ ISTE KLINIKE
                for(OperationRoomDTO or : availableRooms){
                    if(or.getClinicId() == ex.getClinic().getId()){
                        //ZAUZMI SOBU

                        OperationRoom operationRoom = operationRoomRepository.findById(or.getRoomId()).orElse(null);
                        if(operationRoom == null){
                            return;
                        }

                        unnaprovedApp.setOperationRoom(operationRoom);
                        appointmentRepository.saveNative(operationRoom.getId(), unnaprovedApp.getId());

                        //SALJI MAIL PACIJENTU
                        Long app = ex.getId();

                        try {
                            this.emailService.sendNotificaitionAsync(patient, "<a href=\"http://localhost:3000/confirm_exam?"+"val=2&app="+app+"\">Confirm</a> <br></br> <a href=\"http://localhost:3000/confirm_exam?"+"val=3&app=" + app + "\""+">Deny</a>", "Appointment confirmation");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //SALJI MAIL DOKTORU

                        List<Doctor> doctors = doctorRepository.findAllByExaminations(ex);

                        if(doctors != null || !doctors.isEmpty()){

                            Doctor doctor = doctors.get(0);

                            try {
                                this.emailService.sendNotificaitionAsync(doctor,
                                        "Appointment has been set. <br></br> Date: " + unnaprovedApp.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() ,
                                        "Appointment confirmation");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        break;
                    }
                }


            } else if (unnaprovedApp instanceof Operation){
                Patient patient = unnaprovedApp.getPatient();
                Operation op = (Operation) unnaprovedApp;

                List<OperationRoomDTO> availableRooms = operationRoomService.getAllAvailable(op.getDate().getTime());
                if(availableRooms.isEmpty() || availableRooms == null){
                    continue;
                }

                for(OperationRoomDTO or : availableRooms){
                    if(or.getClinicId() == op.getClinic().getId()){
                        //ZAUZMI SOBU

                        OperationRoom operationRoom = operationRoomRepository.findById(or.getRoomId()).orElse(null);
                        if(operationRoom == null){
                            return;
                        }

                        unnaprovedApp.setOperationRoom(operationRoom);
                        appointmentRepository.saveNative(operationRoom.getId(), unnaprovedApp.getId());

                        //SALJI MAIL PACIJENTU

                        try {
                            this.emailService.sendNotificaitionAsync(patient, "Appointment has been set. <br></br> Date: " + unnaprovedApp.getDate() + "<br></br>Room: " + unnaprovedApp.getOperationRoom().getName() + " " + unnaprovedApp.getOperationRoom().getNumber(), "Appointment confirmation");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //SALJI MAIL DOKTORU

                        List<Doctor> doctors = doctorRepository.findAllByOperations(op);

                        if (doctors != null || !doctors.isEmpty()) {

                            for (Doctor dr : doctors) {
                                try {
                                    this.emailService.sendNotificaitionAsync(dr,
                                            "Appointment has been set. <br></br> Date: " + unnaprovedApp.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() + "<br></br>Room: " + unnaprovedApp.getOperationRoom().getName() + " " + unnaprovedApp.getOperationRoom().getNumber(),
                                            "Appointment confirmation");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            throw new NotValidParamsException("There isn't any doctor");
                        }

                        break;
                    }
                }

            }
        }
    }

    @Override
    public AppointmentDTO changeDoctorAndAddRoom(ChangeDoctorRequestDTO changeDoctorRequestDTO) {

        Appointment appointment = appointmentRepository.findById(changeDoctorRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(changeDoctorRequestDTO.getRoomId()).orElseThrow(NotExistsException::new);
        Doctor doctor = doctorRepository.findById(changeDoctorRequestDTO.getDoctorId()).orElseThrow(NotExistsException::new);

        if(appointment instanceof Examination){
            Examination ex = (Examination) appointment;
            List<Doctor> doctors = doctorRepository.findAllByExaminations(ex);
            if(!doctors.isEmpty() || doctors != null){
                Doctor currentDoctor = doctors.get(0);
                currentDoctor.getExaminations().remove(ex);
            }else {
                throw new NotExistsException();
            }

            Patient patient = appointment.getPatient();
            doctor.getExaminations().add(ex);
            appointment.setOperationRoom(operationRoom);


            Long app = ex.getId();
            //POSALJI MAIL DOKTORU
            try {
                this.emailService.sendNotificaitionAsync(doctor,
                        "Appointment has been set. <br></br> Date: " + appointment.getDate() + "<br></br> Patient: " + patient.getName() + " " + patient.getLastName() ,
                        "Appointment confirmation");
            }catch (Exception e){
                e.printStackTrace();
            }

            //POSALJI MAIL PACIJENTU
            try {
                this.emailService.sendNotificaitionAsync(patient, "<a href=\"http://localhost:3000/confirm_exam?"+"val=2&app="+app+"\">Confirm</a> <br></br> <a href=\"http://localhost:3000/confirm_exam?"+"val=3&app=" + app + "\""+">Deny</a>", "Appointment confirmation");
            }catch (Exception e){
                e.printStackTrace();
            }

            doctorRepository.save(doctor);
            appointmentRepository.save(appointment);

            return new AppointmentDTO(appointment);
        }

        return null;
    }


    @Override
    public List<AppointmentDTO> getAllHeldBetweenNowAndEnd(ClinicReviewRequestDTO interval) {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);
        Long clinicId = adminClinic.getClinic().getId();

        Date startDate = interval.getStartDate();
        Date endDate = interval.getEndDate();

        String pattern = "yyyy-MM-dd hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        System.out.println(startDateString);
        System.out.println(endDateString);

        List<Appointment> appointments = appointmentRepository.findAllHeldAndFromClinicBetweenNowAndEndDate(clinicId, startDate, endDate).orElseThrow(NotExistsException::new);
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        for(Appointment a : appointments){
            appointmentDTOS.add(new AppointmentDTO(a));
        }

        return appointmentDTOS;
    }
  
  

    // tries to schedule an appointment for selected date and time and doctor if he is available
    @Override
    public void reserveExaminationForPatient(Long selected_date, int hours, int minutes, Long doc_id, Long service_id) {
        Long millis_in_hour = 3600000L;
        Long millis_in_min = 60000L;
        Long duration = 1800000L;
        Long my_date = selected_date + (hours * millis_in_hour) + (minutes * millis_in_min);

        //DATUMI PODESENI

        com.proj.medicalClinic.model.Service my_service = serviceRepository.findById(service_id).orElseThrow(NotExistsException::new);
        Doctor my_doc = doctorRepository.findById(doc_id).orElseThrow(NotExistsException::new);

        List<AppUser> nurses = appUserRepository.findByUserRole(RoleType.NURSE).orElse(null);
        Nurse assignedNurse = null;

        for(AppUser ap: nurses) {
            Nurse nr = (Nurse) ap;
            if (nr.getClinic().getId() == my_doc.getClinic().getId()) {
                boolean allowNurse = true;
                List<Appointment> appointmentsNurse = appointmentRepository.findAllByNurse(nr.getId());
                List<Leave> leavesNurse = leaveRepository.findAllByNurse(nr);

                for (Appointment o : appointmentsNurse) {
                    if ((o.getDate().getTime() < my_date) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > my_date)) {
                        System.out.println("UDJE 1");
                        allowNurse = false;
                        break;
                    }

                    if ((o.getDate().getTime() < (my_date + 30 * millis_in_min)) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > (my_date + 30 * millis_in_min))) {
                        System.out.println("UDJE 2");
                        allowNurse = false;
                        break;
                    }
                }

                if (allowNurse) {
                    for (Leave l : leavesNurse) {
                        if ((l.getDateStart().getTime() < my_date) && (l.getDateEnd().getTime() > my_date)) {
                            System.out.println("UDJE 3");
                            allowNurse = false;
                            break;
                        }
                        if ((l.getDateStart().getTime() < (my_date + 30 * millis_in_min)) && (l.getDateEnd().getTime() > (my_date + 30 * millis_in_min))) {
                            System.out.println("UDJE 4");
                            allowNurse = false;
                            break;
                        }
                    }
                    if(allowNurse) {
                        System.out.println("Asisgned nurse " + nr.getId() + " " + nr.getEmail());
                        assignedNurse = nr;
                        break;
                    }
                }
            }
        }

        if (assignedNurse == null) {
            assignedNurse = (Nurse) nurses.get(-1);
            System.out.println("Assigned last nurse " + assignedNurse.getId() + " " + assignedNurse.getEmail());
        }

        // check shifts
//        if(my_doc.getShift() == 1){
//            if(hours >= 8){
//                throw new NotExistsException();
//            }
//        }
//        else if(my_doc.getShift() == 2){
//            if(hours < 8 || hours >= 16){
//                throw new NotExistsException();
//            }
//        }
//        else{
//            if(hours < 16){
//                throw new NotExistsException();
//            }
//        }


        List<Operation> operations = operationRepository.findAllByDoctorsContaining(my_doc);
        List<Examination> examinations = examinationRepository.findAllByDoctorsContainingAndConfirmed(my_doc, 2);
        List<Leave> leaves = leaveRepository.findAllByDoctor(my_doc);

        for(Operation o : operations){
            // ako pocetak upada izmedju neko app
            if((o.getDate().getTime() < my_date) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > my_date)){
                System.out.println("UDJE JEDAN");
                throw new NotExistsException();
            }
            // ako kraj upada izmedju nekog app
            if((o.getDate().getTime() < (my_date + 30 * millis_in_min)) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE DVA");
                throw new NotExistsException();
            }
        }

        for(Examination o : examinations){
            if((o.getDate().getTime() < my_date) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > my_date)){
                System.out.println("UDJE TRI");
                throw new NotExistsException();
            }
            if((o.getDate().getTime() < (my_date + 30 * millis_in_min)) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE CETIRI");
                throw new NotExistsException();
            }
        }

        for(Leave l : leaves){
            if((l.getDateStart().getTime() < my_date) && (l.getDateEnd().getTime() > my_date)){
                System.out.println("UDJE PET");
                throw new NotExistsException();
            }
            if((l.getDateStart().getTime() < (my_date + 30 * millis_in_min)) && (l.getDateEnd().getTime() > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE SEST");
                throw new NotExistsException();
            }
        }

        Examination ex = new Examination();
        List<Doctor> list_doc = new ArrayList<Doctor>();
        list_doc.add(my_doc);
        ex.setDoctors(list_doc);
        ex.setFast(false);
        ex.setHeld(false);
        ex.setNurse(null);
        ex.setOperationRoom(null);
        ex.setDate(new Date(my_date));
        ex.setDuration(30);
        // kako preuzeti pacijenta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println(username);
        Patient p =(Patient) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        ex.setPatient(p);
        ex.setClinic(my_doc.getClinic());
        ex.setService(my_service);

        examinationRepository.save(ex);

        List<Examination> ex_for_doc = new ArrayList<>();
        ex_for_doc = my_doc.getExaminations();
        ex_for_doc.add(ex);
        my_doc.setExaminations(ex_for_doc);

        doctorRepository.save(my_doc);

        return;
    }


    @Override
    @Transactional
    public boolean addNextForPatient(NetxAppointmentRequestDTO nextAppointment) {

        Appointment lastApp = appointmentRepository.findById(nextAppointment.getLastAppointmentId()).orElseThrow(NotExistsException::new);

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        Doctor doctor = (Doctor) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);
        ArrayList<Doctor> appointmentDoctors = new ArrayList<>();
        appointmentDoctors.add(doctor);

        OperationRoom operationRoom = lastApp.getOperationRoom();

        Date selectedDate = new Date(nextAppointment.getStartDate());

        List<OperationRoomDTO> availableRooms = operationRoomService.getAllAvailable(nextAppointment.getStartDate());

        if(availableRooms.isEmpty()){
            throw new NotExistsException();
        }

        boolean isAvailable = false;

        for(OperationRoomDTO or : availableRooms){
            if(or.getRoomId() == operationRoom.getId()){
                isAvailable = true;
            }
        }


        if(isAvailable){
            if(nextAppointment.getAppointmentType().equals("Examination")){
                Examination ex = (Examination) lastApp;
                Nurse nurse = ex.getNurse();

                Examination newExamination = new Examination();
                newExamination.setDate(selectedDate);
                newExamination.setOperationRoom(null);
                newExamination.setDoctors(appointmentDoctors);
                newExamination.setService(lastApp.getService());
                newExamination.setDuration(30);
                newExamination.setFast(false);
                newExamination.setPatient(lastApp.getPatient());
                newExamination.setHeld(false);
                newExamination.setNurse(nurse);
                newExamination.setMReport(null);
                newExamination.setConfirmed(1);
                newExamination.setClinic(lastApp.getClinic());

                examinationRepository.save(newExamination);

                doctor.getExaminations().add(newExamination);
                doctorRepository.save(doctor);

                long clinicId = doctor.getClinic().getId();

                Clinic clinic = clinicRepository.findById(clinicId).orElse(null);
                if(clinic ==  null){
                    throw new NotExistsException();
                }

                List<AdminClinic> adminClinics = adminClinicRepository.findAllByClinic(clinic);
                if(adminClinics.isEmpty() || adminClinics == null){
                    throw new NotExistsException();
                }

                for(AdminClinic ac : adminClinics){
                    try {
                        this.emailService.sendNotificaitionAsync(ac,"New appointment request has been created" ,"Appointment request");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                return true;

            }else if(nextAppointment.getAppointmentType().equals("Operation")){

                    Operation operation = new Operation();
                    operation.setDate(selectedDate);
                    operation.setOperationRoom(null);
                    operation.setDoctors(appointmentDoctors);
                    operation.setService(lastApp.getService());
                    operation.setDuration(30);
                    operation.setPatient(lastApp.getPatient());
                    operation.setClinic(lastApp.getClinic());
                    operation.setFast(false);
                    operation.setHeld(false);


                    operationRepository.save(operation);
                    doctor.getOperations().add(operation);
                    doctorRepository.save(doctor);

                    String queryMR = "UPDATE appointment SET confirmed = ?1 WHERE id = ?2";
                    Query queryMREm = em.createNativeQuery(queryMR)
                            .setParameter(1, 2)
                            .setParameter(2, operation.getId());
                    em.joinTransaction();
                    queryMREm.executeUpdate();

                long clinicId = doctor.getClinic().getId();

                Clinic clinic = clinicRepository.findById(clinicId).orElse(null);
                if(clinic ==  null){
                    throw new NotExistsException();
                }

                List<AdminClinic> adminClinics = adminClinicRepository.findAllByClinic(clinic);
                if(adminClinics.isEmpty() || adminClinics == null){
                    throw new NotExistsException();
                }

                for(AdminClinic ac : adminClinics){
                    try {
                        this.emailService.sendNotificaitionAsync(ac,"New appointment request has been created" ,"Appointment request");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
  
    // returns list of all available fast appointments in selected clinic
    @Override
    public List<FastExamDTO> findAllFastForClinic(Long clinic_id) {
        Date date = new Date();
        List<Appointment> apps = appointmentRepository.findAllByClinicIdAndDateAfterAndPatientId(clinic_id, date, null);


        if(apps.isEmpty()){
            throw new NotExistsException();
        }

        List<FastExamDTO> ret = new ArrayList<>();
        for(Appointment a : apps){
            Examination ap = examinationRepository.findById(a.getId()).orElseThrow(NotExistsException::new);
            List<Doctor> docs = doctorRepository.findAllByExaminations(ap);
            System.out.println(ap.getId());
            ret.add(new FastExamDTO(ap, docs.get(0)));
        }

        return ret;
    }

    @Override
    public void reserveFastAppointment(Long appointment_id) {
        Examination ex = examinationRepository.findById(appointment_id).orElseThrow(NotExistsException::new);
        Appointment ap = appointmentRepository.findById(appointment_id).orElseThrow(NotExistsException::new);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println(username);
        Patient p =(Patient) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        ex.setPatient(p);
        List<Appointment> a = p.getAppointments();
        a.add(ap);
        p.setAppointments(a);

        Doctor d = doctorRepository.findAllByExaminations(ex).get(0);
        Clinic c = clinicRepository.findByDoctorId(d.getId()).orElseThrow(NotExistsException::new);

        List<Patient> patients_clinic = c.getPatients();
        if(!patients_clinic.contains(p)){
            patients_clinic.add(p);
            c.setPatients(patients_clinic);
            clinicRepository.save(c);
        }

        examinationRepository.save(ex);
        appUserRepository.save(p);

        try {
            emailService.sendNotificaitionAsync((AppUser) p, "Brz pregled uspesno rezervisan", "Potvrda rezervacije brzog pregleda");

        }catch( Exception e ){
        }
    }



}
