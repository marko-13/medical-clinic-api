package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.AppointmentDTO;
import com.proj.medicalClinic.dto.OperationRoomDTO;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.AppUserRepository;
import com.proj.medicalClinic.repository.AppointmentRepository;
import com.proj.medicalClinic.repository.ClinicRepository;
import com.proj.medicalClinic.repository.OperationRoomRepository;
import com.proj.medicalClinic.security.TokenUtils;
import com.proj.medicalClinic.service.AppointmentService;
import com.proj.medicalClinic.service.ClinicService;
import com.proj.medicalClinic.service.OperationRoomService;
import com.sun.deploy.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class OperationRoomServiceImpl implements OperationRoomService {

    @Autowired
    private OperationRoomRepository operationRoomRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public List<OperationRoomDTO> getAll() {
        List<OperationRoom> operationRooms = operationRoomRepository.findAllByDeletedNot(true);
        List<OperationRoomDTO> operationRoomsDTO = new ArrayList<>();

        for(OperationRoom or : operationRooms){
            operationRoomsDTO.add(new OperationRoomDTO(or));
        }

        return operationRoomsDTO;
    }

    @Override
    public OperationRoomDTO remove(Long roomId) {
            List<AppointmentDTO> appointmentDTOS = appointmentService.getAllByOperationRoom(roomId);

            if(appointmentDTOS.isEmpty()){

                OperationRoom operationRoom = operationRoomRepository.findById(roomId).orElseThrow(NotExistsException::new);
                System.out.println(operationRoom.getName());
                operationRoom.setDeleted(true);
                operationRoomRepository.save(operationRoom);
                return new OperationRoomDTO(operationRoom);

            }else {

                throw new ResourceConflictException(roomId, "Soba ima rezervisane preglede!");
            }
    }

    @Override
    public OperationRoomDTO save(OperationRoomDTO operationRoomRequest) {

        String email = this.tokenUtils.getUsernameFromToken(this.tokenUtils.getToken(this.httpServletRequest));
        AdminClinic appUser = (AdminClinic) customUserDetailsService.loadUserByUsername(email);
        Clinic clinic = clinicRepository.findById(appUser.getClinic().getId()).orElseThrow(NotExistsException::new);

        OperationRoom operationRoom = new OperationRoom();
        operationRoom.setName(operationRoomRequest.getName());
        operationRoom.setNumber(operationRoomRequest.getNumber());
        operationRoom.setClinic(clinic);
        operationRoom.setDeleted(false);

        operationRoomRepository.save(operationRoom);
        return new OperationRoomDTO(operationRoom);
    }

    @Override
    public OperationRoomDTO update(OperationRoomDTO operationRoomRequest) {
        OperationRoom operationRoom = operationRoomRepository.findById(operationRoomRequest.getRoomId()).orElseThrow(NotExistsException::new);
        List<AppointmentDTO> appointmentDTOS = appointmentService.getAllByOperationRoom(operationRoomRequest.getRoomId());
            if(appointmentDTOS.isEmpty()){
                operationRoom.setNumber(operationRoomRequest.getNumber());
                operationRoom.setName(operationRoomRequest.getName());

                operationRoomRepository.save(operationRoom);

                try{

                    Thread.sleep(30000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return new OperationRoomDTO(operationRoom);
            }

        throw new ResourceConflictException(operationRoomRequest.getRoomId(), "Soba ima rezervisane preglede!");
    }

    @Override
    public List<OperationRoomDTO> getAllAvailable(long selectedDate) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(selectedDate);

        c.add(Calendar.DAY_OF_MONTH, -1);
        Date dayBefore = c.getTime();

        c.add(Calendar.DAY_OF_MONTH, 2);
        Date dayAfter = c.getTime();

        System.out.println(dayBefore);
        System.out.println(dayAfter);

        List<Appointment> appointmentsBetweenDates = appointmentService.getAllDayBeforeAndDayAfter(dayBefore, dayAfter);
        if(appointmentsBetweenDates == null){
            System.out.println("NIJE NASAO NI JEDAN IZMEDJU");
            return getAll();
        }

        List<OperationRoom> availableRooms = operationRoomRepository.findAllByDeletedNot(true);
        List<OperationRoomDTO> operationRoomDTOS = new ArrayList<>();
        for(Appointment appointment : appointmentsBetweenDates){
            long appStart = appointment.getDate().getTime();
            long appEnd = (long) (appointment.getDate().getTime() + appointment.getDuration() * 60000);

            if(selectedDate < (appStart - 30 * 60000) || selectedDate > (appEnd)){
                System.out.println("SLOBODAN TERMIN");
            }else {
                System.out.println("ZAUZET");
                for(int i = 0; i < availableRooms.size(); i++){
                    if (availableRooms.get(i).getId() == appointment.getOperationRoom().getId()){
                        availableRooms.remove(i);
                    }
                }
            }
        }

        for(OperationRoom or : availableRooms){
            System.out.println(or.getName() + " OVU SOBU DODAJE");
            operationRoomDTOS.add(new OperationRoomDTO(or));
        }

        return operationRoomDTOS;
    }

    @Override
    public List<OperationRoomDTO> getAllFromClinic() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) userRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Clinic c = adminClinic.getClinic();

        List<OperationRoom> operationRooms = operationRoomRepository.findAllByClinic(c).orElseThrow(NotExistsException::new);
        List<OperationRoomDTO> operationRoomDTOS = new ArrayList<>();


        for(OperationRoom or : operationRooms){
            operationRoomDTOS.add(new OperationRoomDTO(or));
        }

        return operationRoomDTOS;
    }


}
