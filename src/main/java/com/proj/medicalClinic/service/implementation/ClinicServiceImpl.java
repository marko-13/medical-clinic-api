package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.ClinicDTO;
import com.proj.medicalClinic.dto.ClinicServiceDTO;
import com.proj.medicalClinic.dto.DoctorDTO;
import com.proj.medicalClinic.dto.DrugsRegistryDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.ClinicService;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClinicServiceImpl implements ClinicService {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public List<ClinicDTO> getAllClinics() {

        List<Clinic> clinics = clinicRepository.findAll();
        List<ClinicDTO> clinicsDTO = new ArrayList<>();
        for (Clinic c : clinics) {
            clinicsDTO.add(new ClinicDTO(c));
        }

        return clinicsDTO;

    }

    @Override
    public List<ClinicDTO> getClinicsOfAdminClinicalCenter(String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof AdminClinicalCenter)) {
                throw new NotValidParamsException("Only admin of the clinical center can see this data");
            }

            List<Clinic> clinics = clinicRepository.findAllByClinicalCenter(((AdminClinicalCenter) user).getClinicalCenter());

            List<ClinicDTO> clinicsDTO = clinics.stream().map(
                    s -> new ClinicDTO(s)
            ).collect(Collectors.toList());

            return clinicsDTO;

        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }

    }

    //Returns the list of clincis the patient has been to
    @Override
    public List<ClinicDTO> getAllAssociatedWithPatient(String patient_email) {
        Patient my_patient = (Patient)appUserRepository.findByEmail(patient_email).
                orElseThrow(NotExistsException::new);

        List<ClinicDTO> ret_val = new ArrayList<>();

        List<Clinic> patients_clinics = clinicRepository.findAllByPatientId(my_patient.getId());

        for(Clinic c : patients_clinics){
            ret_val.add(new ClinicDTO(c));
        }

        return ret_val;
    }

    // Update broja review-a i zbira svih rview-a klinike
    @Override
    public void review_clinic(Long id, int score) {
        Clinic c = clinicRepository.findById(id).orElseThrow(NotExistsException::new);

        c.setReviewCount(c.getReviewCount() + 1);
        c.setReview(c.getReview() + (float)score);

        clinicRepository.save(c);
    }


    // Returns list of clinics where its possible to get selected service(appropriate doctors exist)
    // and where are appointemnts available for selected date
    @Override
    public List<ClinicServiceDTO> findCorresponding(Long service_id, Long appointment_date, double min_clinic_score) {
        Long eight_hrs_in_miliseconds = 28800000L;
        Long one_hour_in_millis = 3600000L; //ONE HOUR

        // kalendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(appointment_date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = cal.getTime();
        //kalendar

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date my_date = new Date(appointment_date);
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


        //Requested service
        com.proj.medicalClinic.model.Service my_service = serviceRepository.findById(service_id).orElseThrow(NotExistsException::new);
        System.out.println("Id trazenog servicea: " + my_service.getId());

        //sve klinike u kojima moze da se izvrsi odredjeni pregled
        List<Clinic> my_clinics = clinicRepository.findByServiceId(service_id);
        for(Clinic c : my_clinics){
            System.out.println("Id klinika gde se moze izvrsiti service: " + c.getId());
        }

        //svi doktori koji rade u tim klinikama
        List<Doctor> my_doctors1 = doctorRepository.findAllByClinicIn(my_clinics);
        for(Doctor d : my_doctors1){
            System.out.println("Id doktora iz klinika: " + d.getId());
        }
        if(my_doctors1.isEmpty()){
            System.out.println("Ne postoje doktori koj izadovoljavaju krietrijum");
            throw new NotExistsException();
        }

        // u my_doctors_pom su svi oni doktori koji nisu na godisnjem iz zeljene klinike
        List<Doctor> my_doctors_pom = new ArrayList<>();
        List<Doctor> temp_docs = new ArrayList<>();
        temp_docs = doctorRepository.findByDateBeforeOrAfter(day1, day2);
        for (Doctor doca : my_doctors1) {
            if (!(temp_docs.contains(doca))) {
                my_doctors_pom.add(doca);
            } else {
                System.out.println("Izbacen zbog godisnjeg: " + doca.getId());
            }
        }
        if(my_doctors_pom.isEmpty()){
            System.out.println("Ne postoje doktori koj izadovoljavaju krietrijum");
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
            System.out.println("Ne postoje doktori koj izadovoljavaju krietrijum");
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


        // za sve doktore koji imaju vise od 1h slobodnog vremena u nizu
        // vrati njihove klinike
        List<ClinicServiceDTO> ok_clincs = new ArrayList<>();
        for(Clinic c1 : my_clinics){
            for(Doctor d1 : ok_docs){
                // System.out.println("EVO EVO EVO");
                if(d1.getClinic().equals(c1)){
                    System.out.println("Ok klinika: " + c1.getId());
                    ok_clincs.add(new ClinicServiceDTO(c1, my_service.getPrice()));
                    break;
                }
            }
        }

        return ok_clincs;
    }

    @Override
    public ClinicDTO addNewClinic(ClinicDTO clinicDTO, String email) {
        try {
            if (clinicDTO == null) {
                throw new NotValidParamsException("Server has not recieved right clinicDTO");
            }

            AdminClinicalCenter adminCC = (AdminClinicalCenter) this.userDetailsService.loadUserByUsername(email);

            if (adminCC == null) {
                throw new NotValidParamsException("Server has not recieved right email of Administrator of the clinical center");
            }

            List<Clinic> uniqueClinic = this.clinicRepository.findAllByNameAndAddress(clinicDTO.getName(), clinicDTO.getAddress());
            if (!(uniqueClinic.isEmpty())) {
                throw new NotValidParamsException("Already exists");
            }

            Clinic newClinic = new Clinic();

            newClinic.setName(clinicDTO.getName());
            newClinic.setAddress(clinicDTO.getAddress());
            newClinic.setDescription(clinicDTO.getDescription());
            newClinic.setReview(0);
            newClinic.setReviewCount(0);
            newClinic.setClinicalCenter(adminCC.getClinicalCenter());

            clinicRepository.save(newClinic);

            ClinicDTO newClinicDTO = new ClinicDTO(newClinic);

            return newClinicDTO;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ClinicDTO getClinicByAdmin(Long adminId) {
        Clinic clinic = clinicRepository.findByDoctorId(adminId).orElseThrow(NotExistsException::new);
        return new ClinicDTO(clinic);
    }

    @Override
    public ClinicDTO save(ClinicDTO clinicRequest){
        Clinic clinic = clinicRepository.findById(clinicRequest.getId()).orElseThrow(NotExistsException::new);
        clinic.setName(clinicRequest.getName());
        clinic.setAddress(clinicRequest.getAddress());
        clinic.setDescription(clinicRequest.getDescription());

        clinicRepository.save(clinic);

        return new ClinicDTO(clinic);
    }
}
