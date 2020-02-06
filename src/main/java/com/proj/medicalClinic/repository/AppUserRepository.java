package com.proj.medicalClinic.repository;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.model.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository <AppUser, Long>{

    List<AppUser> findAll();

    Optional<List<AppUser>> findByUserRole(RoleType role);

    Optional<List<AppUser>> findAllByEnabledAndRejected(Boolean enabled, Boolean rejected);

    Optional<AppUser> findByEmail(String email);

}
