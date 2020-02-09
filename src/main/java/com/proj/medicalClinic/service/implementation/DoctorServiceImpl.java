package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AppointmentRequestDTO;
import com.proj.medicalClinic.dto.ClinicServiceDTO;
import com.proj.medicalClinic.dto.DoctorDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppUserService;
import com.proj.medicalClinic.service.AuthorityService;
import com.proj.medicalClinic.service.DoctorService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.print.Doc;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;


    public List<DoctorDTO> getAll(){
        List<Doctor> doctors = doctorRepository.findAllByDeletedNot(true);
        if(doctors == null){
            throw new NotExistsException();
        }

        List<DoctorDTO> doctorDTOS = new ArrayList<>();
        for(Doctor d : doctors){
            doctorDTOS.add(new DoctorDTO(d));
        }

        return doctorDTOS;
    }

    // Pronalazi sve doktore koji su pregledali pacijenta
    @Override
    public List<DoctorDTO> getAllAssociatedWithPatient(String patient_email) {
        Patient my_patient = (Patient)userRepository.findByEmail(patient_email).orElseThrow(NotExistsException::new);

        List<Appointment> patients_appointmetns = appointmentRepository.findAllByPatientId(my_patient.getId()).
                orElseThrow(NotExistsException::new);

        List<DoctorDTO> ret_val = new ArrayList<>();

        for(Appointment a : patients_appointmetns){
            if(a instanceof Examination) {
                List<Doctor> my_doctors = doctorRepository.findByPatientAndExamination(a.getId());

                for (Doctor d : my_doctors) {
                    DoctorDTO temp_doc_DTO = new DoctorDTO(d);
                    System.out.println(temp_doc_DTO.toString());
                    if (!(ret_val.contains(temp_doc_DTO))) {
                        ret_val.add(temp_doc_DTO);
                    }
                }
            }
            else if(a instanceof Operation){
                List<Doctor> my_doctors = doctorRepository.findByPatientAndOperation(a.getId());

                for (Doctor d : my_doctors) {
                    DoctorDTO temp_doc_DTO = new DoctorDTO(d);
                    System.out.println(temp_doc_DTO.toString());
                    if (!(ret_val.contains(temp_doc_DTO))) {
                        ret_val.add(temp_doc_DTO);
                    }
                }
            }
        }

        return ret_val;
    }

    @Override
    public List<DoctorDTO> getAllFromClinicAndIsNotDeleted() {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) userRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Clinic c = adminClinic.getClinic();

        List<Doctor> doctors = doctorRepository.findAllByClinicAndDeletedNot(c, true);

        if(doctors == null || doctors.isEmpty()){
            throw new NotExistsException();
        }

        List<DoctorDTO> doctorDTOS = new ArrayList<>();
        for(Doctor d : doctors){
            doctorDTOS.add(new DoctorDTO(d));
        }

        return doctorDTOS;
    }

    // Update broja review-a i zbira svih rview-a doktora
    @Override
    public void review_doctor(Long id, int score) {
        Doctor d = (Doctor)userRepository.findById(id).orElseThrow(NotExistsException::new);

        d.setReviewCount(d.getReviewCount() + 1);
        d.setReview(d.getReview() + (float)score);

        doctorRepository.save(d);
    }

    // returns list of doctors that cen perform given examination for given date and work in given clinic
    @Override
    public List<DoctorDTO> getAllAvailableForExam(Long clinc_id, Long selected_date, Long service_id) {
        // kalendar
        Long eight_hrs_in_miliseconds = 28800000L;
        Long one_hour_in_millis = 3600000L; //ONE HOUR

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selected_date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = cal.getTime();
        //kalendar

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date my_date = new Date(selected_date);
        String my_date1 = df.format(my_date);
        String my_date2 = df.format(dayBefore);

        LocalDate localDate = LocalDate.parse(my_date1);
        LocalDate localDate2 = LocalDate.parse(my_date2);
        LocalDateTime day_beginning = LocalDateTime.of(localDate2, LocalTime.MAX);
        LocalDateTime day_ending = LocalDateTime.of(localDate, LocalTime.MAX);

        LocalDateTime start_app_date = LocalDateTime.of(localDate, LocalTime.MIN);

        Date day1 = java.sql.Timestamp.valueOf(day_beginning);
        Date day2 = java.sql.Timestamp.valueOf(day_ending);
        //pocetak dana da mogu da proverim da li ima mesta za appointment
        Date start_dates = java.sql.Timestamp.valueOf(start_app_date);
        //DATUMI PODESENI
        //--------------------------------------------------------------------------------------------------------------

        com.proj.medicalClinic.model.Service my_service = serviceRepository.findById(service_id).orElseThrow(NotExistsException::new);

        Clinic my_clinc = clinicRepository.getOne(clinc_id);

        //NASAO SVE DOKTORE KOJI RADE U ZELJENOJ KLINICI
        List<Doctor> my_docs_clinic = doctorRepository.findAllByClinic(my_clinc);
        for(Doctor d : my_docs_clinic){
            System.out.println("NASAO DOKTORA IZ KLINIKE(" + my_clinc.getId() + "): " + d.getId());
        }



        // u my_doctors_pom su svi oni doktori koji nisu na godisnjem iz zeljene klinike
        List<Doctor> my_doctors_pom = new ArrayList<>();
        List<Doctor> temp_docs = new ArrayList<>();
        temp_docs = doctorRepository.findByDateBeforeOrAfter(day1, day2);
        for (Doctor doca : my_docs_clinic) {
            if (!(temp_docs.contains(doca))) {
                my_doctors_pom.add(doca);
            } else {
                System.out.println("Izbacen zbog godisnjeg: " + doca.getId());
            }
        }
        if(my_doctors_pom.isEmpty()){
            System.out.println("Ne postoje doktori koj izadovoljavaju krietrijum da nisu na godisnjem");
            throw new NotExistsException();
        }

        // u my_doctors su svi oni doktori koji mogu da obave zeljeni service
        List<Doctor> my_doctors = new ArrayList<>();
        List<Doctor> temp_docs2 = new ArrayList<>();
        temp_docs2 = doctorRepository.findAllByServices(my_service);
        for(Doctor doca2 : my_doctors_pom) {
            if((temp_docs2.contains(doca2))){
                my_doctors.add(doca2);
            }
            else{
                System.out.println("Izbacen jer ne obavlja service: " + doca2.getId());
            }
        }
        if(my_doctors.isEmpty()){
            System.out.println("Ne postoje doktori koj izadovoljavaju krietrijum da mogu da obave zeljeni pregled");
            throw new NotExistsException();
        }






        // liste svih pregleda i operacija za trazeni dan
        List<Examination> examinations = examinationRepository.findAllByDateBetween(day1, day2);
        List<Operation> operations = operationRepository.findAllByDateBetween(day1, day2);


//        Map<Long, Long> appointment_vremena = new HashMap<Long, Long>();
        //MAPA GDE JE KLJUC ID DOKTORA A VREDNOST JE LISTA VREMENA
        Map<Doctor, Map<Long, Double>> my_map = new HashMap<>();
        for(Doctor d : my_doctors){
            my_map.put(d, new HashMap<Long,Double>());
        }


        // ako doktor iz operacije nije u doktorima klinike izbaci
        for(Examination ex : examinations){
            for(Doctor d : ex.getDoctors()){
                // ako taj pregled nema sve doktore iz zeljene klinike izbaci
                if(!(my_doctors.contains(d))){
                    examinations.remove(ex);
                    System.out.println("Nasao pregled za zeljeni dan koji nema doktore iz klinike: " + ex.getId());
                    continue;
                }
////                appointment_vremena.put(ex.getId(), ex.getDate().getTime());
                Map<Long, Double> temp_map = new HashMap<>();
                temp_map.putAll(my_map.get(d));
                temp_map.put(ex.getDate().getTime(), ex.getDuration());
                my_map.replace(d, temp_map);
            }
            System.out.println("NADJEN OK PREGLED: " + ex.getId());
        }
        for(Operation ex : operations){
            for(Doctor d : ex.getDoctors()){
                // ako taj pregled nema sve doktore iz zeljene klinike izbac
                if(!(my_doctors.contains(d))){
                    operations.remove(ex);
                    System.out.println("Nasao operaciju za zeljeni dan koja nema doktore iz klinike: " + ex.getId());
                    continue;
                }
////                appointment_vremena.put(ex.getId(), ex.getDate().getTime());
                Map<Long, Double> temp_map = new HashMap<>();
                temp_map.putAll(my_map.get(d));
                // mnozim sa 60000 jer je u minutama a treba prebaciti u milices
                temp_map.put(ex.getDate().getTime(), ex.getDuration() * 60000);
                my_map.replace(d, temp_map);
            }
            System.out.println("NADJEN OK PREGLED: " + ex.getId());
        }


        List<Doctor> ok_docs = new ArrayList<>();
        //nema doktora nema nicega u klinici, ovo ne bi trebalo nikad da se desi
        if(my_map.isEmpty()){
            System.out.println("Nista nije nasao");
            ok_docs = my_doctors;
        }


        // SORTIRANJE REZULTATA I TRAZENJE PRAZNINA U RASPOREDU
        // prodji kroz sve elemente mape i za svakog doktora sortiraj po redu raspored
        for(Map.Entry<Doctor, Map<Long, Double>> entry : my_map.entrySet()){

            Doctor d = entry.getKey();
            Map<Long, Double> list_of_appointments = entry.getValue();

            System.out.println("DOKTOR: " + d.getId());
            //ako nema appointmenta u listi app samo dodaj doktora i break
            if(list_of_appointments.isEmpty()){
                System.out.println("Nema appointmenta za doktora: " + d.getName());
                ok_docs.add(d);
                continue;
            }


            // SORTIRAJ SVE OPERACIJE I PREGLEDE PO VREMENU. REZ SORTIRANJA JE U TEMP
            //ali sortiraj samo ako ih ima vise od dva
            Map<Long, Double> temp = new HashMap<Long, Double>();
            if(list_of_appointments.size() > 1) {
                // Create a list from elements of HashMap
                List<Map.Entry<Long, Double>> list =
                        new LinkedList<Map.Entry<Long, Double>>(list_of_appointments.entrySet());

                // Sort the list
                Collections.sort(list, new Comparator<Map.Entry<Long, Double>>() {
                    public int compare(Map.Entry<Long, Double> o1,
                                       Map.Entry<Long, Double> o2) {
                        return (o1.getKey()).compareTo(o2.getKey());
                    }
                });

                // put data from sorted list to hashmap
                for (Map.Entry<Long, Double> aa : list) {
                    temp.put(aa.getKey(), aa.getValue());
                }
            }
            else{
                temp = list_of_appointments;
            }
            // -----------------------------------------------------
            my_map.replace(d, temp);




            //E SAD ZA SVAKI TEMP PROVERI DA LI IMA DOVOLJNO VELIKA RUPA DA FITUJE PREGLED
            // prolazi kroz sve

            //OVDE PUKNE ALI ZASTOOOOOOOO
            Collection<Double> trajanja = temp.values();
            Set<Long> vreme_poc = temp.keySet();


            List<Long> vreme_poc_lista = new ArrayList<>();
            vreme_poc_lista.addAll(vreme_poc);
            List<Double> trajanja_lista = new ArrayList<>();
            trajanja_lista.addAll(trajanja);


            for(int i=0; i<vreme_poc_lista.size()-1; i++){
                if(i == 0){
                    if(vreme_poc_lista.get(i) - start_dates.getTime() > one_hour_in_millis){
                        ok_docs.add(d);
                        break;
                    }
                }
                else {
                    if(vreme_poc_lista.get(i + 1) - (vreme_poc_lista.get(i) + trajanja_lista.get(i)) > one_hour_in_millis){
                        ok_docs.add(d);
                        break;
                    }
                }
            }
        }


        List<DoctorDTO> ok_docs_DTO = new ArrayList<>();
        // za sve doktore koji imaju vise od 1h slobodnog vremena u nizu
        // vrati njihove klinike
        for(Doctor d1 : ok_docs){
            // System.out.println("EVO EVO EVO");
            ok_docs_DTO.add(new DoctorDTO(d1));
        }

        if(ok_docs_DTO.isEmpty()){
            throw new NotExistsException();
        }
        return ok_docs_DTO;
    }


    public DoctorDTO save(Doctor doctorRequest) {
        Doctor doctor = new Doctor();

        doctor.setName(doctorRequest.getName());
        doctor.setAdress(doctorRequest.getAdress());
        doctor.setCity(doctorRequest.getCity());
        doctor.setEmail(doctorRequest.getEmail());
        doctor.setLastName(doctorRequest.getLastName());
        doctor.setMobile(doctorRequest.getMobile());
        doctor.setPassword(passwordEncoder.encode("krokodil"));
        doctor.setState(doctorRequest.getState());
        doctor.setUserRole(RoleType.DOCTOR);
        doctor.setReview(0);
        doctor.setReviewCount(0);
        doctor.setShift(doctorRequest.getShift());
        doctor.setEnabled(true);
        doctor.setDeleted(false);
        doctor.setRejected(false);

        List<Authority> auth = authorityService.findByName(doctorRequest.getUserRole().name());
        doctor.setAuthorities(auth);
        doctorRepository.save(doctor);

        return new DoctorDTO(doctor);
    }

    public DoctorDTO remove(Long id){

        Doctor doctor = doctorRepository.findById(id).orElseThrow(NotExistsException::new);

        if(doctor.getExaminations().isEmpty() && doctor.getOperations().isEmpty()){
            doctor.setDeleted(true);
            doctorRepository.save(doctor);
        }else{
            throw new ResourceConflictException(id, "Doktor ima zakazane preglede!");
        }

        return new DoctorDTO(doctor);
    }

    @Override
    public List<DoctorDTO> getAllAvailableForDate(AppointmentRequestDTO appointmentRequestDTO) {

        Appointment appointment;
        Clinic clinic;
        List<Doctor> doctors;
        List<DoctorDTO> availableDoctors = new ArrayList<>();

        System.out.println(appointmentRequestDTO.getAppId());


        if(appointmentRequestDTO.getAppId() == 0){
            System.out.println("ULAZI IZ NEW MEDICAL EXAM");
            Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
            String username = currentUser.getName();

            AdminClinic adminClinic = (AdminClinic) userRepository.findByEmail(username).orElseThrow(NotExistsException::new);

            clinic = adminClinic.getClinic();
            doctors = doctorRepository.findAllByClinicAndDeletedNot(clinic, true);

        }else {
            System.out.println("ULAZI PREKO SEARCH");
            appointment = appointmentRepository.findById(appointmentRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
            clinic = appointment.getClinic();
            doctors = doctorRepository.findAllByClinicAndDeletedNot(clinic, true);
        }

//        Appointment appointment = appointmentRepository.findById(appointmentRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
//        Clinic clinic = appointment.getClinic();
//        List<Doctor> doctors = doctorRepository.findAllByClinicAndDeletedNot(clinic, true);
//        List<DoctorDTO> availableDoctors = new ArrayList<>();

        for(Doctor d : doctors){
            availableDoctors.add(new DoctorDTO(d));
        }

        long selectedDate = appointmentRequestDTO.getStart();
        for(Doctor d : doctors){
            for(Examination e : d.getExaminations()){
                long exStart = e.getDate().getTime();
                long exEnd = (long) (exStart + e.getDuration() * 60000);

                if(selectedDate >= exStart && selectedDate <= exEnd) {
                    for(int i = 0; i < availableDoctors.size(); i++){
                        if(availableDoctors.get(i).getId() == d.getId()){
                            System.out.println("DOKTOR IZBRISAN " + d.getName());
                            availableDoctors.remove(i);
                            break;
                        }
                    }
                }
            }

            for(Operation o : d.getOperations()){
                long oStart = o.getDate().getTime();
                long oEnd = (long) (oStart + o.getDuration() * 60000);

                if(selectedDate >= oStart && selectedDate <= oEnd){
                    for(int i = 0; i < availableDoctors.size(); i++){
                        if(availableDoctors.get(i).getId() == d.getId()){
                            System.out.println("DOKTOR IZBRISAN " + d.getName());
                            availableDoctors.remove(i);
                            break;
                        }
                    }
                }
            }
        }
        return availableDoctors;
    }

}
