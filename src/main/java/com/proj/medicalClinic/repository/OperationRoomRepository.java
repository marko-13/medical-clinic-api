package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.Clinic;
import com.proj.medicalClinic.model.OperationRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationRoomRepository extends JpaRepository<OperationRoom, Long> {
    List<OperationRoom> findAllByDeletedNot(boolean deleted);
    Optional<List<OperationRoom>> findAllByClinic(Clinic clinic);
}
