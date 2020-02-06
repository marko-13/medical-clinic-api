package com.proj.medicalClinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", unique = false, nullable = false)
    private String name;

    @Column(name = "address", unique = false, nullable = false)
    private String address;

    @Column(name = "description", unique = false, nullable = false)
    private String description;

    @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nurse> nurses;

    @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperationRoom> operationRooms;

    @ManyToMany
    @JoinTable(
            name = "Clinics_Services",
            joinColumns = {@JoinColumn(name = "clinic_id")},
            inverseJoinColumns = {@JoinColumn(name = "service_id")}
    )
    private List<Service> services;

    @ManyToMany
    @JoinTable(
            name = "Clinics_Patients",
            joinColumns = {@JoinColumn(name = "clinic_id")},
            inverseJoinColumns = {@JoinColumn(name = "patient_id")}
    )
    private List<Patient> patients;

    @Column(name = "review", unique = false, nullable = true)
    private double review;

    @Column(name = "review_count", unique = false, nullable = false)
    private int reviewCount;

    @ManyToOne
    @JoinColumn(name = "clinical_center_id", nullable = false)
    private ClinicalCenter clinicalCenter;

    @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdminClinic> adminsClinic;
}
